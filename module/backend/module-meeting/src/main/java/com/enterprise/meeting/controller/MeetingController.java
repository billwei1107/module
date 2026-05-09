package com.enterprise.meeting.controller;

import com.enterprise.common.dto.ApiResponse;
import com.enterprise.meeting.dto.MeetingDTOs.*;
import com.enterprise.meeting.service.MeetingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * @file MeetingController.java
 * @description 會議管理控制器 / Meeting controller
 */
@RestController
@RequestMapping("/api/v1/meetings")
@RequiredArgsConstructor
public class MeetingController {
    private final MeetingService meetingService;

    @PostMapping("/rooms") public ApiResponse<RoomDTO> createRoom(@RequestBody CreateRoomRequest request) { return ApiResponse.success(meetingService.createRoom(request)); }
    @GetMapping("/rooms") public ApiResponse<List<RoomDTO>> getRooms() { return ApiResponse.success(meetingService.getRooms()); }
    @PostMapping("/bookings") public ApiResponse<BookingDTO> createBooking(@RequestBody CreateBookingRequest request) { return ApiResponse.success(meetingService.createBooking(request)); }
    @GetMapping("/bookings") public ApiResponse<List<BookingDTO>> getBookings() { return ApiResponse.success(meetingService.getBookings()); }
    @PostMapping public ApiResponse<MeetingDTO> createMeeting(@RequestBody CreateMeetingRequest request) { return ApiResponse.success(meetingService.createMeeting(request)); }
    @GetMapping public ApiResponse<List<MeetingDTO>> getMeetings() { return ApiResponse.success(meetingService.getMeetings()); }
    @PostMapping("/minutes") public ApiResponse<MinuteDTO> createMinute(@RequestBody CreateMinuteRequest request) { return ApiResponse.success(meetingService.createMinute(request)); }
    @GetMapping("/{meetingId}/action-items") public ApiResponse<List<ActionItemDTO>> getActionItems(@PathVariable String meetingId) { return ApiResponse.success(meetingService.getActionItems(meetingId)); }
    @PostMapping("/action-items/{id}/complete") public ApiResponse<ActionItemDTO> completeActionItem(@PathVariable String id) { return ApiResponse.success(meetingService.completeActionItem(id)); }
}
