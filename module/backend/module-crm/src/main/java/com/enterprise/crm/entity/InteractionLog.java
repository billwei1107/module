package com.enterprise.crm.entity;

import com.enterprise.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @file InteractionLog.java
 * @description 客戶互動紀錄實體 / Customer interaction log entity
 * @description_en Stores visits, calls, and follow-up reminders
 * @description_zh 儲存拜訪、通話與後續跟進提醒
 */
@Entity
@Table(name = "crm_interaction_logs")
@Data
@EqualsAndHashCode(callSuper = true)
public class InteractionLog extends BaseEntity {
    public enum InteractionType { VISIT, CALL, EMAIL, MEETING, FOLLOW_UP }

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;
    @Column(name = "contact_id")
    private UUID contactId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InteractionType type = InteractionType.FOLLOW_UP;
    @Column(name = "handled_by", length = 80)
    private String handledBy;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String note;
    @Column(name = "next_follow_up_at")
    private LocalDateTime nextFollowUpAt;
    @Column(nullable = false)
    private Boolean completed = false;
}
