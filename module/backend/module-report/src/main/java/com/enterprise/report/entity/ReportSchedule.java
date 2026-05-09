package com.enterprise.report.entity;

import com.enterprise.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @file ReportSchedule.java
 * @description 報表排程實體 / Report schedule entity
 * @description_en Stores report generation cadence and delivery metadata
 * @description_zh 儲存報表產生週期與寄送中繼資料
 */
@Entity
@Table(name = "rpt_schedules")
@Data
@EqualsAndHashCode(callSuper = true)
public class ReportSchedule extends BaseEntity {

    @Column(name = "definition_id", nullable = false)
    private UUID definitionId;

    @Column(name = "cron_expression", nullable = false, length = 120)
    private String cronExpression;

    @Column(name = "recipient_emails", nullable = false, columnDefinition = "TEXT")
    private String recipientEmails = "";

    @Column(name = "last_run_at")
    private LocalDateTime lastRunAt;

    @Column(nullable = false)
    private Boolean active = true;
}
