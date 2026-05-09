package com.enterprise.payroll.dto;

import com.enterprise.payroll.entity.SalaryItem.CalculationType;
import com.enterprise.payroll.entity.SalaryItem.ItemCategory;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @file CreateSalaryItemRequest.java
 * @description 建立薪資項目請求 / Create salary item request
 */
@Data
public class CreateSalaryItemRequest {
    private String name;
    private String code;
    private ItemCategory category;
    private CalculationType calculationType = CalculationType.FIXED;
    private BigDecimal amount = BigDecimal.ZERO;
    private BigDecimal percentage = BigDecimal.ZERO;
}
