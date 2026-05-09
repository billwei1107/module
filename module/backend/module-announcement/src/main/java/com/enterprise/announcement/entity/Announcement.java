package com.enterprise.announcement.entity;

import com.enterprise.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

/**
 * @file Announcement.java
 * @description 公告實體 / Announcement entity
 * @description_en Stores announcement content, schedule, status, and receipt requirement
 * @description_zh 儲存公告內容、排程、狀態與回條需求
 */
@Entity
@Table(name = "ann_announcements")
@Getter
@Setter
public class Announcement extends BaseEntity {
    public enum AnnouncementStatus { DRAFT, SCHEDULED, PUBLISHED, ARCHIVED }

    @Column(nullable = false, length = 160)
    private String title;
    @Column(nullable = false, length = 4000)
    private String content;
    @Column(length = 80)
    private String category;
    @Column(length = 500)
    private String attachmentUrl;
    @Column(length = 64)
    private String publisherId;
    private LocalDateTime scheduledPublishAt;
    private LocalDateTime scheduledUnpublishAt;
    @Column(nullable = false)
    private Boolean requiresConfirmation = false;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AnnouncementStatus status = AnnouncementStatus.SCHEDULED;
}
