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
 * @file SalaryStructure.java
 * @description 薪資結構實體 / Salary structure entity
 * @description_en Stores employee salary basis such as monthly, hourly, or daily pay
 * @description_zh 儲存員工月薪、時薪或日薪制的薪資基礎
 */
@Entity
@Table(name = "pay_salary_structures")
@Data
@EqualsAndHashCode(callSuper = true)
public class SalaryStructure extends BaseEntity {

    public enum SalaryType {
        MONTHLY, HOURLY, DAILY
    }

    @Column(nullable = false, length = 120)
    private String name;

    @Column(name = "employee_id", nullable = false, unique = true, length = 80)
    private String employeeId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SalaryType type = SalaryType.MONTHLY;

    @Column(name = "base_salary", nullable = false, precision = 19, scale = 4)
    private BigDecimal baseSalary = BigDecimal.ZERO;

    @Column(name = "hourly_rate", nullable = false, precision = 19, scale = 4)
    private BigDecimal hourlyRate = BigDecimal.ZERO;

    @Column(nullable = false)
    private Boolean active = true;
}
