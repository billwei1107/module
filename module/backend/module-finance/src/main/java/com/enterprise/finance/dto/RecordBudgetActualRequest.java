package com.enterprise.finance.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @file RecordBudgetActualRequest.java
 * @description 記錄預算實際支出請求 / Record budget actual request
 */
@Data
public class RecordBudgetActualRequest {
    private String accountId;
    private BigDecimal amount = BigDecimal.ZERO;
}
