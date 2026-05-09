package com.enterprise.payroll.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @file PayrollDetailDTO.java
 * @description 薪資明細回傳資料 / Payroll detail response DTO
 */
@Data
@Builder
public class PayrollDetailDTO {
    private String id;
    private String itemCode;
    private String itemName;
    private String category;
    private BigDecimal amount;
    private String description;
}
