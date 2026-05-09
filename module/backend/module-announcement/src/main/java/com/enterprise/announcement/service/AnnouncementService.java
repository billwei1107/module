package com.enterprise.announcement.service;

import com.enterprise.announcement.dto.AnnouncementDTOs.*;
import java.util.List;

/**
 * @file AnnouncementService.java
 * @description 公告系統服務介面 / Announcement service interface
 */
public interface AnnouncementService {
    AnnouncementDTO createAnnouncement(CreateAnnouncementRequest request);
    List<AnnouncementDTO> getAnnouncements();
    int publishDueAnnouncements();
    int archiveExpiredAnnouncements();
    List<AnnouncementDTO> getVisibleAnnouncements(VisibleAnnouncementQuery query);
    AnnouncementDTO markRead(String announcementId, String userId);
    AnnouncementDTO confirm(String announcementId, String userId);
}
