package com.enterprise.project.entity;

import com.enterprise.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @file TimeLog.java
 * @description 工時紀錄實體 / Time log entity
 * @description_en Stores manual and timer-based work logs for tasks
 * @description_zh 儲存任務手動填寫或計時產生的工時紀錄
 */
@Entity
@Table(name = "proj_time_logs")
@Data
@EqualsAndHashCode(callSuper = true)
public class TimeLog extends BaseEntity {

    @Column(name = "task_id", nullable = false)
    private UUID taskId;

    @Column(name = "employee_id", nullable = false, length = 80)
    private String employeeId;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @Column(name = "minutes", nullable = false)
    private Integer minutes = 0;

    @Column(columnDefinition = "TEXT")
    private String note;
}
