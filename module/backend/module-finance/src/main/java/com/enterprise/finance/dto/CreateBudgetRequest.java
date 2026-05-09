package com.enterprise.finance.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @file CreateBudgetRequest.java
 * @description 建立預算請求 / Create budget request
 */
@Data
public class CreateBudgetRequest {
    private String name;
    private String departmentId;
    private Integer fiscalYear;
    private BigDecimal totalAmount = BigDecimal.ZERO;
}
