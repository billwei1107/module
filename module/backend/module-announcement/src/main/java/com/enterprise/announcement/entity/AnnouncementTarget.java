package com.enterprise.announcement.entity;

import com.enterprise.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

/**
 * @file AnnouncementTarget.java
 * @description 公告發布範圍實體 / Announcement target entity
 * @description_en Stores announcement audience scope records
 * @description_zh 儲存公告發布對象範圍
 */
@Entity
@Table(name = "ann_targets")
@Getter
@Setter
public class AnnouncementTarget extends BaseEntity {
    public enum TargetType { ALL_COMPANY, COMPANY, DEPARTMENT, USER }

    @Column(nullable = false)
    private UUID announcementId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TargetType targetType = TargetType.ALL_COMPANY;
    @Column(length = 64)
    private String targetId;
}
