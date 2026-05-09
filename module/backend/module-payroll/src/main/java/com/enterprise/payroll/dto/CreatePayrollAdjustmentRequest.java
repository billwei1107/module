package com.enterprise.payroll.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @file CreatePayrollAdjustmentRequest.java
 * @description 建立薪資調整請求 / Create payroll adjustment request
 */
@Data
public class CreatePayrollAdjustmentRequest {
    private String employeeId;
    private String yearMonth;
    private String adjustmentType;
    private BigDecimal amount = BigDecimal.ZERO;
    private String description;
}
