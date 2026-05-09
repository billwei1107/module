package com.enterprise.finance.entity;

import com.enterprise.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * @file Budget.java
 * @description 預算主檔實體 / Budget entity
 * @description_en Stores department fiscal-year budget header data
 * @description_zh 儲存部門年度預算主檔資料
 */
@Entity
@Table(name = "fin_budgets")
@Data
@EqualsAndHashCode(callSuper = true)
public class Budget extends BaseEntity {

    @Column(nullable = false, length = 120)
    private String name;

    @Column(name = "department_id", length = 80)
    private String departmentId;

    @Column(name = "fiscal_year", nullable = false)
    private Integer fiscalYear;

    @Column(name = "total_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal totalAmount = BigDecimal.ZERO;
}
