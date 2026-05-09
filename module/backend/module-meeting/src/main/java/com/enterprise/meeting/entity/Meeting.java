package com.enterprise.meeting.entity;

import com.enterprise.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @file Meeting.java
 * @description 會議實體 / Meeting entity
 * @description_en Stores meeting topic, agenda, schedule, and lifecycle status
 * @description_zh 儲存會議主題、議程、時間與生命週期狀態
 */
@Entity
@Table(name = "meet_meetings")
@Getter
@Setter
public class Meeting extends BaseEntity {
    public enum MeetingStatus { SCHEDULED, COMPLETED, CANCELLED }

    private UUID bookingId;
    @Column(nullable = false, length = 160)
    private String subject;
    @Column(length = 64)
    private String organizerId;
    @Column(length = 2000)
    private String agenda;
    @Column(nullable = false)
    private LocalDateTime startTime;
    @Column(nullable = false)
    private LocalDateTime endTime;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MeetingStatus status = MeetingStatus.SCHEDULED;
}
