package com.enterprise.finance.entity;

import com.enterprise.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * @file BudgetItem.java
 * @description 預算明細實體 / Budget item entity
 * @description_en Tracks planned and actual amounts by account
 * @description_zh 依會計科目追蹤預算編列與實際支出金額
 */
@Entity
@Table(name = "fin_budget_items")
@Data
@EqualsAndHashCode(callSuper = true)
public class BudgetItem extends BaseEntity {

    @Column(name = "budget_id", nullable = false)
    private UUID budgetId;

    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    @Column(name = "planned_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal plannedAmount = BigDecimal.ZERO;

    @Column(name = "actual_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal actualAmount = BigDecimal.ZERO;
}
