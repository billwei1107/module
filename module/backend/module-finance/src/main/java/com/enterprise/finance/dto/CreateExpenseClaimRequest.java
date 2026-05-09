package com.enterprise.finance.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @file CreateExpenseClaimRequest.java
 * @description 建立報銷申請請求 / Create expense claim request
 */
@Data
public class CreateExpenseClaimRequest {
    private String employeeId;
    private BigDecimal amount = BigDecimal.ZERO;
    private String category;
    private String description;
}
