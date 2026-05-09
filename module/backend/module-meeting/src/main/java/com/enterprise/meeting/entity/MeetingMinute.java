package com.enterprise.meeting.entity;

import com.enterprise.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

/**
 * @file MeetingMinute.java
 * @description 會議紀錄實體 / Meeting minute entity
 * @description_en Stores meeting notes and decisions
 * @description_zh 儲存會議紀錄內容與決議
 */
@Entity
@Table(name = "meet_minutes")
@Getter
@Setter
public class MeetingMinute extends BaseEntity {
    @Column(nullable = false)
    private UUID meetingId;
    @Column(length = 64)
    private String authorId;
    @Column(nullable = false, length = 4000)
    private String content;
    @Column(length = 3000)
    private String decisions;
}
