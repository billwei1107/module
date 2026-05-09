package com.enterprise.leave.entity;

import com.enterprise.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @file LeaveType.java
 * @description 假別設定實體 / Leave type entity
 * @description_zh 定義特休、病假、事假等假別與年度配額規則
 */
@Entity
@Table(name = "leave_types")
@Data
@EqualsAndHashCode(callSuper = true)
public class LeaveType extends BaseEntity {

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, unique = true, length = 30)
    private String code;

    @Column(name = "annual_quota_hours", nullable = false)
    private Integer annualQuotaHours = 0;

    @Column(name = "requires_approval", nullable = false)
    private Boolean requiresApproval = true;

    @Column(name = "paid", nullable = false)
    private Boolean paid = true;

    @Column(name = "active", nullable = false)
    private Boolean active = true;
}
