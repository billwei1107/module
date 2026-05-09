package com.enterprise.meeting.dto;

import com.enterprise.meeting.entity.ActionItem.ActionItemStatus;
import com.enterprise.meeting.entity.Meeting.MeetingStatus;
import com.enterprise.meeting.entity.RoomBooking.BookingStatus;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @file MeetingDTOs.java
 * @description 會議管理 DTO 集合 / Meeting DTO collection
 * @description_en Defines meeting module request and response data structures
 * @description_zh 定義會議管理模組請求與回傳資料結構
 */
public final class MeetingDTOs {
    private MeetingDTOs() {}

    @Data public static class CreateRoomRequest { private String name; private String location; private Integer capacity; private String equipment; }
    @Data public static class CreateBookingRequest { private String roomId; private String title; private String organizerId; private LocalDateTime startTime; private LocalDateTime endTime; }
    @Data public static class CreateMeetingRequest { private String bookingId; private String subject; private String organizerId; private String agenda; private LocalDateTime startTime; private LocalDateTime endTime; private List<AttendeeRequest> attendees; }
    @Data public static class AttendeeRequest { private String attendeeId; private String attendeeName; private String email; }
    @Data public static class CreateMinuteRequest { private String meetingId; private String authorId; private String content; private String decisions; private List<ActionItemRequest> actionItems; }
    @Data public static class ActionItemRequest { private String description; private String ownerId; private LocalDate dueDate; }

    @Data @Builder public static class RoomDTO { private String id; private String name; private String location; private Integer capacity; private String equipment; private Boolean active; }
    @Data @Builder public static class BookingDTO { private String id; private String roomId; private String title; private String organizerId; private LocalDateTime startTime; private LocalDateTime endTime; private BookingStatus status; }
    @Data @Builder public static class MeetingDTO { private String id; private String bookingId; private String subject; private String organizerId; private String agenda; private LocalDateTime startTime; private LocalDateTime endTime; private MeetingStatus status; private List<AttendeeDTO> attendees; }
    @Data @Builder public static class AttendeeDTO { private String id; private String meetingId; private String attendeeId; private String attendeeName; private String email; private String response; }
    @Data @Builder public static class MinuteDTO { private String id; private String meetingId; private String authorId; private String content; private String decisions; private List<ActionItemDTO> actionItems; }
    @Data @Builder public static class ActionItemDTO { private String id; private String meetingId; private String minuteId; private String description; private String ownerId; private LocalDate dueDate; private ActionItemStatus status; }
}
