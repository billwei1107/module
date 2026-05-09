package com.enterprise.announcement.entity;

import com.enterprise.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @file AnnouncementRead.java
 * @description 公告已讀紀錄實體 / Announcement read receipt entity
 * @description_en Tracks which users have read announcements
 * @description_zh 追蹤使用者公告已讀狀態
 */
@Entity
@Table(name = "ann_reads")
@Getter
@Setter
public class AnnouncementRead extends BaseEntity {
    @Column(nullable = false)
    private UUID announcementId;
    @Column(nullable = false, length = 64)
    private String userId;
    @Column(nullable = false)
    private LocalDateTime readAt;
}
