package com.enterprise.payroll.dto;

import com.enterprise.payroll.entity.SalaryStructure.SalaryType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @file SalaryStructureDTO.java
 * @description 薪資結構回傳資料 / Salary structure response DTO
 */
@Data
@Builder
public class SalaryStructureDTO {
    private String id;
    private String name;
    private String employeeId;
    private SalaryType type;
    private BigDecimal baseSalary;
    private BigDecimal hourlyRate;
    private Boolean active;
}
