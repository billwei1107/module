package com.enterprise.payroll.controller;

import com.enterprise.common.dto.ApiResponse;
import com.enterprise.payroll.dto.CreateSalaryItemRequest;
import com.enterprise.payroll.dto.CreateSalaryStructureRequest;
import com.enterprise.payroll.dto.SalaryItemDTO;
import com.enterprise.payroll.dto.SalaryStructureDTO;
import com.enterprise.payroll.service.PayrollSetupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @file PayrollSetupController.java
 * @description 薪資設定控制器 / Payroll setup controller
 */
@RestController
@RequestMapping("/api/v1/payroll")
@RequiredArgsConstructor
public class PayrollSetupController {

    private final PayrollSetupService payrollSetupService;

    @GetMapping("/salary-structures")
    public ApiResponse<List<SalaryStructureDTO>> getSalaryStructures() {
        return ApiResponse.success(payrollSetupService.getSalaryStructures());
    }

    @PutMapping("/salary-structures")
    public ApiResponse<SalaryStructureDTO> upsertSalaryStructure(@RequestBody CreateSalaryStructureRequest request) {
        return ApiResponse.success(payrollSetupService.upsertSalaryStructure(request));
    }

    @GetMapping("/salary-items")
    public ApiResponse<List<SalaryItemDTO>> getSalaryItems() {
        return ApiResponse.success(payrollSetupService.getSalaryItems());
    }

    @PostMapping("/salary-items")
    public ApiResponse<SalaryItemDTO> createSalaryItem(@RequestBody CreateSalaryItemRequest request) {
        return ApiResponse.success(payrollSetupService.createSalaryItem(request));
    }
}
