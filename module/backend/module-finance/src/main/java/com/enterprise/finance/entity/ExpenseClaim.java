package com.enterprise.finance.entity;

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
 * @file ExpenseClaim.java
 * @description 費用報銷實體 / Expense claim entity
 * @description_en Stores employee reimbursement requests and approval state
 * @description_zh 儲存員工費用報銷申請與審批狀態
 */
@Entity
@Table(name = "fin_expense_claims")
@Data
@EqualsAndHashCode(callSuper = true)
public class ExpenseClaim extends BaseEntity {

    public enum ExpenseClaimStatus {
        DRAFT, PENDING, APPROVED, REJECTED, PAID
    }

    @Column(name = "employee_id", nullable = false, length = 80)
    private String employeeId;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount = BigDecimal.ZERO;

    @Column(nullable = false, length = 80)
    private String category;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ExpenseClaimStatus status = ExpenseClaimStatus.DRAFT;

    @Column(name = "workflow_instance_id", length = 80)
    private String workflowInstanceId;
}
