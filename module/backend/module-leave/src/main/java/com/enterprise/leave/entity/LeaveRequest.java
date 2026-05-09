package com.enterprise.leave.entity;

import com.enterprise.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @file LeaveRequest.java
 * @description 請假申請實體 / Leave request entity
 * @description_zh 記錄員工請假期間、原因、審批狀態與流程實例
 */
@Entity
@Table(name = "leave_requests")
@Data
@EqualsAndHashCode(callSuper = true)
public class LeaveRequest extends BaseEntity {

    @Column(name = "employee_id", nullable = false, length = 36)
    private String employeeId;

    @Column(name = "leave_type_id", nullable = false)
    private UUID leaveTypeId;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "hours", nullable = false)
    private Integer hours;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Column(nullable = false, length = 20)
    private String status = "PENDING";

    @Column(name = "workflow_instance_id", length = 36)
    private String workflowInstanceId;

    @Column(name = "reviewed_by", length = 36)
    private String reviewedBy;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;
}
