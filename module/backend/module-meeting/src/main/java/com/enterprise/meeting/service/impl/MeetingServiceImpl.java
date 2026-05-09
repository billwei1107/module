package com.enterprise.meeting.service.impl;

import com.enterprise.common.annotation.Auditable;
import com.enterprise.common.event.SystemNotificationEvent;
import com.enterprise.common.exception.BusinessException;
import com.enterprise.meeting.dto.MeetingDTOs.*;
import com.enterprise.meeting.entity.*;
import com.enterprise.meeting.entity.ActionItem.ActionItemStatus;
import com.enterprise.meeting.entity.RoomBooking.BookingStatus;
import com.enterprise.meeting.repository.*;
import com.enterprise.meeting.service.MeetingService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @file MeetingServiceImpl.java
 * @description 會議管理服務實作 / Meeting service implementation
 * @description_en Handles rooms, bookings, conflict checks, attendee notices, minutes, and action items
 * @description_zh 處理會議室、預約衝突、與會者通知、會議紀錄與決議追蹤
 */
@Service
@RequiredArgsConstructor
public class MeetingServiceImpl implements MeetingService {
    private final MeetingRoomRepository roomRepository;
    private final RoomBookingRepository bookingRepository;
    private final MeetingRepository meetingRepository;
    private final MeetingAttendeeRepository attendeeRepository;
    private final MeetingMinuteRepository minuteRepository;
    private final ActionItemRepository actionItemRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public RoomDTO createRoom(CreateRoomRequest request) {
        MeetingRoom room = new MeetingRoom();
        room.setName(required(request.getName(), "會議室名稱不可為空 / Room name is required"));
        room.setLocation(request.getLocation());
        room.setCapacity(request.getCapacity() == null ? 0 : Math.max(request.getCapacity(), 0));
        room.setEquipment(request.getEquipment());
        return toDTO(roomRepository.save(room));
    }

    @Override
    public List<RoomDTO> getRooms() {
        return roomRepository.findByDeletedAtIsNullOrderByNameAsc().stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional
    @Auditable(module = "meeting", action = "CREATE_ROOM_BOOKING")
    public BookingDTO createBooking(CreateBookingRequest request) {
        UUID roomId = UUID.fromString(required(request.getRoomId(), "會議室不可為空 / Room is required"));
        roomRepository.findById(roomId).filter(room -> room.getDeletedAt() == null && Boolean.TRUE.equals(room.getActive()))
                .orElseThrow(() -> new BusinessException(404, "會議室不存在或停用 / Meeting room not found or inactive"));
        LocalDateTime startTime = requiredTime(request.getStartTime(), "開始時間不可為空 / Start time is required");
        LocalDateTime endTime = requiredTime(request.getEndTime(), "結束時間不可為空 / End time is required");
        if (!endTime.isAfter(startTime)) {
            throw new BusinessException(400, "結束時間必須晚於開始時間 / End time must be after start time");
        }
        List<RoomBooking> conflicts = bookingRepository.findByRoomIdAndStatusAndEndTimeAfterAndStartTimeBeforeAndDeletedAtIsNull(roomId, BookingStatus.BOOKED, startTime, endTime);
        if (!conflicts.isEmpty()) {
            throw new BusinessException(409, "會議室時段已被預約 / Meeting room booking conflicts with existing reservation");
        }
        RoomBooking booking = new RoomBooking();
        booking.setRoomId(roomId);
        booking.setTitle(required(request.getTitle(), "預約標題不可為空 / Booking title is required"));
        booking.setOrganizerId(request.getOrganizerId());
        booking.setStartTime(startTime);
        booking.setEndTime(endTime);
        return toDTO(bookingRepository.save(booking));
    }

    @Override
    public List<BookingDTO> getBookings() {
        return bookingRepository.findByDeletedAtIsNullOrderByStartTimeDesc().stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional
    public MeetingDTO createMeeting(CreateMeetingRequest request) {
        Meeting meeting = new Meeting();
        meeting.setBookingId(parseUuid(request.getBookingId()));
        meeting.setSubject(required(request.getSubject(), "會議主題不可為空 / Meeting subject is required"));
        meeting.setOrganizerId(request.getOrganizerId());
        meeting.setAgenda(request.getAgenda());
        meeting.setStartTime(requiredTime(request.getStartTime(), "開始時間不可為空 / Start time is required"));
        meeting.setEndTime(requiredTime(request.getEndTime(), "結束時間不可為空 / End time is required"));
        Meeting saved = meetingRepository.save(meeting);
        List<MeetingAttendee> attendees = (request.getAttendees() == null ? List.<AttendeeRequest>of() : request.getAttendees()).stream()
                .map(attendeeRequest -> createAttendee(saved.getId(), attendeeRequest))
                .map(attendeeRepository::save)
                .toList();
        attendees.forEach(attendee -> publishMeetingNotice(saved, attendee));
        return toDTO(saved, attendees);
    }

    @Override
    public List<MeetingDTO> getMeetings() {
        return meetingRepository.findByDeletedAtIsNullOrderByStartTimeDesc().stream()
                .map(meeting -> toDTO(meeting, attendeeRepository.findByMeetingIdAndDeletedAtIsNullOrderByCreatedAtAsc(meeting.getId())))
                .toList();
    }

    @Override
    @Transactional
    public MinuteDTO createMinute(CreateMinuteRequest request) {
        Meeting meeting = findMeeting(request.getMeetingId());
        MeetingMinute minute = new MeetingMinute();
        minute.setMeetingId(meeting.getId());
        minute.setAuthorId(request.getAuthorId());
        minute.setContent(required(request.getContent(), "會議紀錄不可為空 / Meeting minute content is required"));
        minute.setDecisions(request.getDecisions());
        MeetingMinute savedMinute = minuteRepository.save(minute);
        List<ActionItem> actionItems = (request.getActionItems() == null ? List.<ActionItemRequest>of() : request.getActionItems()).stream()
                .map(actionItemRequest -> createActionItem(meeting.getId(), savedMinute.getId(), actionItemRequest))
                .map(actionItemRepository::save)
                .toList();
        return toDTO(savedMinute, actionItems);
    }

    @Override
    @Transactional
    public ActionItemDTO completeActionItem(String id) {
        ActionItem item = actionItemRepository.findById(UUID.fromString(id)).filter(actionItem -> actionItem.getDeletedAt() == null)
                .orElseThrow(() -> new BusinessException(404, "決議追蹤項目不存在 / Action item not found"));
        item.setStatus(ActionItemStatus.DONE);
        return toDTO(actionItemRepository.save(item));
    }

    @Override
    public List<ActionItemDTO> getActionItems(String meetingId) {
        return actionItemRepository.findByMeetingIdAndDeletedAtIsNullOrderByCreatedAtAsc(UUID.fromString(meetingId)).stream().map(this::toDTO).toList();
    }

    private MeetingAttendee createAttendee(UUID meetingId, AttendeeRequest request) {
        MeetingAttendee attendee = new MeetingAttendee();
        attendee.setMeetingId(meetingId);
        attendee.setAttendeeId(required(request.getAttendeeId(), "與會者不可為空 / Attendee is required"));
        attendee.setAttendeeName(request.getAttendeeName());
        attendee.setEmail(request.getEmail());
        return attendee;
    }

    private ActionItem createActionItem(UUID meetingId, UUID minuteId, ActionItemRequest request) {
        ActionItem item = new ActionItem();
        item.setMeetingId(meetingId);
        item.setMinuteId(minuteId);
        item.setDescription(required(request.getDescription(), "決議追蹤內容不可為空 / Action item description is required"));
        item.setOwnerId(request.getOwnerId());
        item.setDueDate(request.getDueDate());
        return item;
    }

    private void publishMeetingNotice(Meeting meeting, MeetingAttendee attendee) {
        eventPublisher.publishEvent(new SystemNotificationEvent(this, attendee.getAttendeeId(), "MEETING_INVITATION", "IN_APP", "meeting",
                Map.of("meetingId", meeting.getId().toString(), "subject", meeting.getSubject())));
    }

    private Meeting findMeeting(String meetingId) {
        return meetingRepository.findById(UUID.fromString(meetingId)).filter(meeting -> meeting.getDeletedAt() == null)
                .orElseThrow(() -> new BusinessException(404, "會議不存在 / Meeting not found"));
    }

    private UUID parseUuid(String value) { return value == null || value.isBlank() ? null : UUID.fromString(value); }
    private LocalDateTime requiredTime(LocalDateTime value, String message) {
        if (value == null) throw new BusinessException(400, message);
        return value;
    }
    private String required(String value, String message) {
        if (value == null || value.isBlank()) throw new BusinessException(400, message);
        return value.trim();
    }

    private RoomDTO toDTO(MeetingRoom room) { return RoomDTO.builder().id(room.getId().toString()).name(room.getName()).location(room.getLocation()).capacity(room.getCapacity()).equipment(room.getEquipment()).active(room.getActive()).build(); }
    private BookingDTO toDTO(RoomBooking booking) { return BookingDTO.builder().id(booking.getId().toString()).roomId(booking.getRoomId().toString()).title(booking.getTitle()).organizerId(booking.getOrganizerId()).startTime(booking.getStartTime()).endTime(booking.getEndTime()).status(booking.getStatus()).build(); }
    private MeetingDTO toDTO(Meeting meeting, List<MeetingAttendee> attendees) { return MeetingDTO.builder().id(meeting.getId().toString()).bookingId(meeting.getBookingId() == null ? null : meeting.getBookingId().toString()).subject(meeting.getSubject()).organizerId(meeting.getOrganizerId()).agenda(meeting.getAgenda()).startTime(meeting.getStartTime()).endTime(meeting.getEndTime()).status(meeting.getStatus()).attendees(attendees.stream().map(this::toDTO).toList()).build(); }
    private AttendeeDTO toDTO(MeetingAttendee attendee) { return AttendeeDTO.builder().id(attendee.getId().toString()).meetingId(attendee.getMeetingId().toString()).attendeeId(attendee.getAttendeeId()).attendeeName(attendee.getAttendeeName()).email(attendee.getEmail()).response(attendee.getResponse().name()).build(); }
    private MinuteDTO toDTO(MeetingMinute minute, List<ActionItem> actionItems) { return MinuteDTO.builder().id(minute.getId().toString()).meetingId(minute.getMeetingId().toString()).authorId(minute.getAuthorId()).content(minute.getContent()).decisions(minute.getDecisions()).actionItems(actionItems.stream().map(this::toDTO).toList()).build(); }
    private ActionItemDTO toDTO(ActionItem item) { return ActionItemDTO.builder().id(item.getId().toString()).meetingId(item.getMeetingId().toString()).minuteId(item.getMinuteId() == null ? null : item.getMinuteId().toString()).description(item.getDescription()).ownerId(item.getOwnerId()).dueDate(item.getDueDate()).status(item.getStatus()).build(); }
}
