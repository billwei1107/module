package com.enterprise.announcement.dto;

import com.enterprise.announcement.entity.Announcement.AnnouncementStatus;
import com.enterprise.announcement.entity.AnnouncementTarget.TargetType;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @file AnnouncementDTOs.java
 * @description 公告系統 DTO 集合 / Announcement DTO collection
 * @description_en Defines announcement request and response data structures
 * @description_zh 定義公告系統模組請求與回傳資料結構
 */
public final class AnnouncementDTOs {
    private AnnouncementDTOs() {}

    @Data public static class CreateAnnouncementRequest { private String title; private String content; private String category; private String attachmentUrl; private String publisherId; private LocalDateTime scheduledPublishAt; private LocalDateTime scheduledUnpublishAt; private Boolean requiresConfirmation; private List<TargetRequest> targets; }
    @Data public static class TargetRequest { private TargetType targetType; private String targetId; }
    @Data public static class VisibleAnnouncementQuery { private String userId; private String companyId; private String departmentId; }

    @Data @Builder public static class AnnouncementDTO { private String id; private String title; private String content; private String category; private String attachmentUrl; private String publisherId; private LocalDateTime scheduledPublishAt; private LocalDateTime scheduledUnpublishAt; private Boolean requiresConfirmation; private AnnouncementStatus status; private List<TargetDTO> targets; private boolean read; private boolean confirmed; private long readCount; private long confirmationCount; }
    @Data @Builder public static class TargetDTO { private String id; private String announcementId; private TargetType targetType; private String targetId; }
}
