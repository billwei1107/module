package com.enterprise.announcement.controller;

import com.enterprise.announcement.dto.AnnouncementDTOs.*;
import com.enterprise.announcement.service.AnnouncementService;
import com.enterprise.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * @file AnnouncementController.java
 * @description 公告系統控制器 / Announcement controller
 */
@RestController
@RequestMapping("/api/v1/announcements")
@RequiredArgsConstructor
public class AnnouncementController {
    private final AnnouncementService announcementService;

    @PostMapping public ApiResponse<AnnouncementDTO> createAnnouncement(@RequestBody CreateAnnouncementRequest request) { return ApiResponse.success(announcementService.createAnnouncement(request)); }
    @GetMapping public ApiResponse<List<AnnouncementDTO>> getAnnouncements() { return ApiResponse.success(announcementService.getAnnouncements()); }
    @PostMapping("/publish-due") public ApiResponse<Integer> publishDueAnnouncements() { return ApiResponse.success(announcementService.publishDueAnnouncements()); }
    @PostMapping("/archive-expired") public ApiResponse<Integer> archiveExpiredAnnouncements() { return ApiResponse.success(announcementService.archiveExpiredAnnouncements()); }
    @PostMapping("/visible") public ApiResponse<List<AnnouncementDTO>> getVisibleAnnouncements(@RequestBody VisibleAnnouncementQuery query) { return ApiResponse.success(announcementService.getVisibleAnnouncements(query)); }
    @PostMapping("/{id}/read") public ApiResponse<AnnouncementDTO> markRead(@PathVariable String id, @RequestParam String userId) { return ApiResponse.success(announcementService.markRead(id, userId)); }
    @PostMapping("/{id}/confirm") public ApiResponse<AnnouncementDTO> confirm(@PathVariable String id, @RequestParam String userId) { return ApiResponse.success(announcementService.confirm(id, userId)); }
}
