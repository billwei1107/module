package com.enterprise.payroll.dto;

import com.enterprise.payroll.entity.PayrollRecord.PayrollStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @file PayrollRecordDTO.java
 * @description 薪資紀錄回傳資料 / Payroll record response DTO
 */
@Data
@Builder
public class PayrollRecordDTO {
    private String id;
    private String employeeId;
    private String yearMonth;
    private BigDecimal baseSalary;
    private BigDecimal totalEarnings;
    private BigDecimal totalDeductions;
    private BigDecimal netPay;
    private PayrollStatus status;
    private List<PayrollDetailDTO> details;
}
