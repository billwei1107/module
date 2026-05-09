package com.enterprise.payroll.dto;

import com.enterprise.payroll.entity.SalaryItem.CalculationType;
import com.enterprise.payroll.entity.SalaryItem.ItemCategory;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @file SalaryItemDTO.java
 * @description 薪資項目回傳資料 / Salary item response DTO
 */
@Data
@Builder
public class SalaryItemDTO {
    private String id;
    private String name;
    private String code;
    private ItemCategory category;
    private CalculationType calculationType;
    private BigDecimal amount;
    private BigDecimal percentage;
    private Boolean active;
}
