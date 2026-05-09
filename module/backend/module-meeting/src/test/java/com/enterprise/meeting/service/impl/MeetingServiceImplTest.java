package com.enterprise.meeting.service.impl;

import com.enterprise.common.event.SystemNotificationEvent;
import com.enterprise.common.exception.BusinessException;
import com.enterprise.meeting.dto.MeetingDTOs.*;
import com.enterprise.meeting.entity.Meeting;
import com.enterprise.meeting.entity.MeetingAttendee;
import com.enterprise.meeting.entity.MeetingRoom;
import com.enterprise.meeting.entity.RoomBooking;
import com.enterprise.meeting.entity.RoomBooking.BookingStatus;
import com.enterprise.meeting.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @file MeetingServiceImplTest.java
 * @description 會議管理服務測試 / Meeting service tests
 */
class MeetingServiceImplTest {

    @Test
    void bookingShouldRejectOverlappingTimeWindow() {
        UUID roomId = UUID.randomUUID();
        RoomBookingRepository bookingRepository = mock(RoomBookingRepository.class);
        when(bookingRepository.findByRoomIdAndStatusAndEndTimeAfterAndStartTimeBeforeAndDeletedAtIsNull(eq(roomId), eq(BookingStatus.BOOKED), any(), any()))
                .thenReturn(List.of(new RoomBooking()));
        MeetingServiceImpl service = service(roomId, bookingRepository, mock(ApplicationEventPublisher.class));
        CreateBookingRequest request = bookingRequest(roomId, LocalDateTime.of(2026, 5, 9, 10, 0), LocalDateTime.of(2026, 5, 9, 11, 0));

        assertThatThrownBy(() -> service.createBooking(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("時段已被預約");
    }

    @Test
    void createMeetingShouldNotifyAttendees() {
        UUID roomId = UUID.randomUUID();
        ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
        MeetingRepository meetingRepository = mock(MeetingRepository.class);
        MeetingAttendeeRepository attendeeRepository = mock(MeetingAttendeeRepository.class);
        when(meetingRepository.save(any())).thenAnswer(invocation -> withId(invocation.getArgument(0, Meeting.class)));
        when(attendeeRepository.save(any())).thenAnswer(invocation -> withId(invocation.getArgument(0, MeetingAttendee.class)));
        MeetingServiceImpl service = service(roomId, mock(RoomBookingRepository.class), meetingRepository, attendeeRepository, eventPublisher);
        CreateMeetingRequest request = new CreateMeetingRequest();
        request.setSubject("週會");
        request.setOrganizerId("manager-1");
        request.setStartTime(LocalDateTime.of(2026, 5, 9, 10, 0));
        request.setEndTime(LocalDateTime.of(2026, 5, 9, 11, 0));
        AttendeeRequest attendee = new AttendeeRequest();
        attendee.setAttendeeId("employee-1");
        request.setAttendees(List.of(attendee));

        service.createMeeting(request);

        verify(eventPublisher).publishEvent(any(SystemNotificationEvent.class));
    }

    private MeetingServiceImpl service(UUID roomId, RoomBookingRepository bookingRepository, ApplicationEventPublisher eventPublisher) {
        return service(roomId, bookingRepository, mock(MeetingRepository.class), mock(MeetingAttendeeRepository.class), eventPublisher);
    }

    private MeetingServiceImpl service(UUID roomId, RoomBookingRepository bookingRepository, MeetingRepository meetingRepository, MeetingAttendeeRepository attendeeRepository, ApplicationEventPublisher eventPublisher) {
        MeetingRoomRepository roomRepository = mock(MeetingRoomRepository.class);
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room(roomId)));
        return new MeetingServiceImpl(roomRepository, bookingRepository, meetingRepository, attendeeRepository, mock(MeetingMinuteRepository.class), mock(ActionItemRepository.class), eventPublisher);
    }

    private CreateBookingRequest bookingRequest(UUID roomId, LocalDateTime start, LocalDateTime end) {
        CreateBookingRequest request = new CreateBookingRequest();
        request.setRoomId(roomId.toString());
        request.setTitle("預約");
        request.setStartTime(start);
        request.setEndTime(end);
        return request;
    }

    private MeetingRoom room(UUID id) {
        MeetingRoom room = new MeetingRoom();
        room.setId(id);
        room.setName("A01");
        room.setActive(true);
        return room;
    }

    private Meeting withId(Meeting meeting) {
        meeting.setId(UUID.randomUUID());
        return meeting;
    }

    private MeetingAttendee withId(MeetingAttendee attendee) {
        attendee.setId(UUID.randomUUID());
        return attendee;
    }
}
