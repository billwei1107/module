package com.enterprise.payroll.entity;

import com.enterprise.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * @file PayrollDetail.java
 * @description 薪資明細實體 / Payroll detail entity
 * @description_en Stores one earning or deduction line for a payroll record
 * @description_zh 儲存薪資紀錄中的單筆給付或扣款明細
 */
@Entity
@Table(name = "pay_payroll_details")
@Data
@EqualsAndHashCode(callSuper = true)
public class PayrollDetail extends BaseEntity {

    @Column(name = "payroll_record_id", nullable = false)
    private UUID payrollRecordId;

    @Column(name = "salary_item_id")
    private UUID salaryItemId;

    @Column(name = "item_code", nullable = false, length = 50)
    private String itemCode;

    @Column(name = "item_name", nullable = false, length = 120)
    private String itemName;

    @Column(name = "category", nullable = false, length = 20)
    private String category;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount = BigDecimal.ZERO;

    @Column(columnDefinition = "TEXT")
    private String description;
}
