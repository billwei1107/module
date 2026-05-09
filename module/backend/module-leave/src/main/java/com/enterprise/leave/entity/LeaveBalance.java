package com.enterprise.leave.entity;

import com.enterprise.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

/**
 * @file LeaveBalance.java
 * @description 員工假別配額實體 / Employee leave balance entity
 * @description_zh 記錄員工每年各假別的可用、已用與保留時數
 */
@Entity
@Table(name = "leave_balances")
@Data
@EqualsAndHashCode(callSuper = true)
public class LeaveBalance extends BaseEntity {

    @Column(name = "employee_id", nullable = false, length = 36)
    private String employeeId;

    @Column(name = "leave_type_id", nullable = false)
    private UUID leaveTypeId;

    @Column(nullable = false)
    private Integer year;

    @Column(name = "total_hours", nullable = false)
    private Integer totalHours = 0;

    @Column(name = "used_hours", nullable = false)
    private Integer usedHours = 0;

    @Column(name = "reserved_hours", nullable = false)
    private Integer reservedHours = 0;
}
