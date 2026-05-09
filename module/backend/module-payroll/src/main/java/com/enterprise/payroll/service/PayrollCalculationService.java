package com.enterprise.payroll.service;

import com.enterprise.payroll.dto.CreatePayrollAdjustmentRequest;
import com.enterprise.payroll.dto.PayrollRecordDTO;

import java.math.BigDecimal;
import java.util.List;

/**
 * @file PayrollCalculationService.java
 * @description 薪資計算服務介面 / Payroll calculation service contract
 */
public interface PayrollCalculationService {
    PayrollRecordDTO calculateMonthly(String employeeId, String yearMonth);

    List<PayrollRecordDTO> getPayrollRecords(String employeeId, String yearMonth);

    PayrollRecordDTO confirmPayrollRecord(String id, String confirmedBy);

    void createAdjustment(CreatePayrollAdjustmentRequest request);

    BigDecimal calculateTax(BigDecimal taxableIncome);

    BigDecimal calculateEmployeeInsurance(BigDecimal salaryBase);
}
