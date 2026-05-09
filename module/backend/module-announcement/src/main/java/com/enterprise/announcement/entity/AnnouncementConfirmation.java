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
 * @file AnnouncementConfirmation.java
 * @description 公告回條實體 / Announcement confirmation entity
 * @description_en Tracks explicit confirmation receipts for important announcements
 * @description_zh 追蹤重要公告的明確回條確認
 */
@Entity
@Table(name = "ann_confirmations")
@Getter
@Setter
public class AnnouncementConfirmation extends BaseEntity {
    @Column(nullable = false)
    private UUID announcementId;
    @Column(nullable = false, length = 64)
    private String userId;
    @Column(nullable = false)
    private LocalDateTime confirmedAt;
}
