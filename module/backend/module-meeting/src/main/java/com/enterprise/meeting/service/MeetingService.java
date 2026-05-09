package com.enterprise.meeting.service;

import com.enterprise.meeting.dto.MeetingDTOs.*;
import java.util.List;

/**
 * @file MeetingService.java
 * @description 會議管理服務介面 / Meeting service interface
 */
public interface MeetingService {
    RoomDTO createRoom(CreateRoomRequest request);
    List<RoomDTO> getRooms();
    BookingDTO createBooking(CreateBookingRequest request);
    List<BookingDTO> getBookings();
    MeetingDTO createMeeting(CreateMeetingRequest request);
    List<MeetingDTO> getMeetings();
    MinuteDTO createMinute(CreateMinuteRequest request);
    ActionItemDTO completeActionItem(String id);
    List<ActionItemDTO> getActionItems(String meetingId);
}
