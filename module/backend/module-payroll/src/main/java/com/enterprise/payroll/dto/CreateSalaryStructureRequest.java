package com.enterprise.payroll.dto;

import com.enterprise.payroll.entity.SalaryStructure.SalaryType;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @file CreateSalaryStructureRequest.java
 * @description 建立薪資結構請求 / Create salary structure request
 */
@Data
public class CreateSalaryStructureRequest {
    private String name;
    private String employeeId;
    private SalaryType type = SalaryType.MONTHLY;
    private BigDecimal baseSalary = BigDecimal.ZERO;
    private BigDecimal hourlyRate = BigDecimal.ZERO;
}
