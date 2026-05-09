package com.enterprise.payroll.entity;

import com.enterprise.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * @file PayrollAdjustment.java
 * @description 薪資計算參考調整 / Payroll adjustment entity
 * @description_en Stores overtime and unpaid leave adjustments collected from events or APIs
 * @description_zh 儲存由事件或 API 收集的加班與無薪假計薪調整
 */
@Entity
@Table(name = "pay_payroll_adjustments")
@Data
@EqualsAndHashCode(callSuper = true)
public class PayrollAdjustment extends BaseEntity {

    @Column(name = "employee_id", nullable = false, length = 80)
    private String employeeId;

    @Column(name = "year_month", nullable = false, length = 7)
    private String yearMonth;

    @Column(name = "adjustment_type", nullable = false, length = 40)
    private String adjustmentType;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount = BigDecimal.ZERO;

    @Column(columnDefinition = "TEXT")
    private String description;
}
