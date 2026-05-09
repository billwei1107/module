package com.enterprise.finance.dto;

import com.enterprise.finance.entity.ExpenseClaim.ExpenseClaimStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @file ExpenseClaimDTO.java
 * @description 報銷申請回傳資料 / Expense claim response DTO
 */
@Data
@Builder
public class ExpenseClaimDTO {
    private String id;
    private String employeeId;
    private BigDecimal amount;
    private String category;
    private String description;
    private ExpenseClaimStatus status;
    private String workflowInstanceId;
}
