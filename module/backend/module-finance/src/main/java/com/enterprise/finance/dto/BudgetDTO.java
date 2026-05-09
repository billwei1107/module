package com.enterprise.finance.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @file BudgetDTO.java
 * @description 預算回傳資料 / Budget response DTO
 */
@Data
@Builder
public class BudgetDTO {
    private String id;
    private String name;
    private String departmentId;
    private Integer fiscalYear;
    private BigDecimal totalAmount;
    private BigDecimal actualAmount;
    private BigDecimal usageRate;
    private Boolean warning;
}
