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

/**
 * @file SalaryItem.java
 * @description 薪資項目實體 / Salary item entity
 * @description_en Defines earnings and deductions used during payroll calculation
 * @description_zh 定義計薪時使用的給付與扣款項目
 */
@Entity
@Table(name = "pay_salary_items")
@Data
@EqualsAndHashCode(callSuper = true)
public class SalaryItem extends BaseEntity {

    public enum ItemCategory {
        EARNING, DEDUCTION
    }

    public enum CalculationType {
        FIXED, PERCENTAGE, FORMULA
    }

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ItemCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "calculation_type", nullable = false, length = 20)
    private CalculationType calculationType = CalculationType.FIXED;

    @Column(precision = 19, scale = 4)
    private BigDecimal amount = BigDecimal.ZERO;

    @Column(precision = 9, scale = 6)
    private BigDecimal percentage = BigDecimal.ZERO;

    @Column(nullable = false)
    private Boolean active = true;
}
