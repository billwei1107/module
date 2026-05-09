package com.enterprise.payroll.entity;

import com.enterprise.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @file PayrollRecord.java
 * @description 薪資紀錄實體 / Payroll record entity
 * @description_en Stores calculated payroll totals for one employee and month
 * @description_zh 儲存單一員工單月份的薪資計算總額
 */
@Entity
@Table(name = "pay_payroll_records")
@Data
@EqualsAndHashCode(callSuper = true)
public class PayrollRecord extends BaseEntity {

    public enum PayrollStatus {
        DRAFT, CONFIRMED, PAID
    }

    @Column(name = "employee_id", nullable = false, length = 80)
    private String employeeId;

    @Column(name = "year_month", nullable = false, length = 7)
    private String yearMonth;

    @Column(name = "base_salary", nullable = false, precision = 19, scale = 4)
    private BigDecimal baseSalary = BigDecimal.ZERO;

    @Column(name = "total_earnings", nullable = false, precision = 19, scale = 4)
    private BigDecimal totalEarnings = BigDecimal.ZERO;

    @Column(name = "total_deductions", nullable = false, precision = 19, scale = 4)
    private BigDecimal totalDeductions = BigDecimal.ZERO;

    @Column(name = "net_pay", nullable = false, precision = 19, scale = 4)
    private BigDecimal netPay = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PayrollStatus status = PayrollStatus.DRAFT;

    @Column(name = "confirmed_by", length = 80)
    private String confirmedBy;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;
}
