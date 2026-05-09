package com.enterprise.payroll.controller;

import com.enterprise.common.dto.ApiResponse;
import com.enterprise.common.security.SecurityUtils;
import com.enterprise.payroll.dto.CreatePayrollAdjustmentRequest;
import com.enterprise.payroll.dto.PayrollRecordDTO;
import com.enterprise.payroll.service.PayrollCalculationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @file PayrollRecordController.java
 * @description 薪資紀錄控制器 / Payroll record controller
 */
@RestController
@RequestMapping("/api/v1/payroll")
@RequiredArgsConstructor
public class PayrollRecordController {

    private final PayrollCalculationService payrollCalculationService;

    @PostMapping("/calculate")
    public ApiResponse<PayrollRecordDTO> calculateMonthly(
            @RequestParam String employeeId,
            @RequestParam String month) {
        return ApiResponse.success(payrollCalculationService.calculateMonthly(employeeId, month));
    }

    @GetMapping("/records")
    public ApiResponse<List<PayrollRecordDTO>> getPayrollRecords(
            @RequestParam(required = false) String employeeId,
            @RequestParam(required = false) String month) {
        return ApiResponse.success(payrollCalculationService.getPayrollRecords(employeeId, month));
    }

    @PostMapping("/records/{id}/confirm")
    public ApiResponse<PayrollRecordDTO> confirmPayrollRecord(@PathVariable String id) {
        return ApiResponse.success(payrollCalculationService.confirmPayrollRecord(id, SecurityUtils.getCurrentUserId()));
    }

    @PostMapping("/adjustments")
    public ApiResponse<Void> createAdjustment(@RequestBody CreatePayrollAdjustmentRequest request) {
        payrollCalculationService.createAdjustment(request);
        return ApiResponse.success();
    }
}
