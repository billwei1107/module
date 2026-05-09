package com.enterprise.meeting.entity;

import com.enterprise.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.UUID;

/**
 * @file ActionItem.java
 * @description 會議決議追蹤項目 / Meeting action item entity
 * @description_en Tracks decisions, owners, due dates, and completion status
 * @description_zh 追蹤會議決議、負責人、期限與完成狀態
 */
@Entity
@Table(name = "meet_action_items")
@Getter
@Setter
public class ActionItem extends BaseEntity {
    public enum ActionItemStatus { OPEN, DONE }

    @Column(nullable = false)
    private UUID meetingId;
    private UUID minuteId;
    @Column(nullable = false, length = 500)
    private String description;
    @Column(length = 64)
    private String ownerId;
    private LocalDate dueDate;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ActionItemStatus status = ActionItemStatus.OPEN;
}
