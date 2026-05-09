package com.enterprise.finance.controller;

import com.enterprise.common.dto.ApiResponse;
import com.enterprise.finance.dto.BudgetDTO;
import com.enterprise.finance.dto.CreateBudgetRequest;
import com.enterprise.finance.dto.RecordBudgetActualRequest;
import com.enterprise.finance.service.BudgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @file BudgetController.java
 * @description 預算控制器 / Budget controller
 */
@RestController
@RequestMapping("/api/v1/finance/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @GetMapping
    public ApiResponse<List<BudgetDTO>> getBudgets() {
        return ApiResponse.success(budgetService.getBudgets());
    }

    @PostMapping
    public ApiResponse<BudgetDTO> createBudget(@RequestBody CreateBudgetRequest request) {
        return ApiResponse.success(budgetService.createBudget(request));
    }

    @PostMapping("/{id}/actuals")
    public ApiResponse<BudgetDTO> recordActual(@PathVariable String id, @RequestBody RecordBudgetActualRequest request) {
        return ApiResponse.success(budgetService.recordActual(id, request));
    }
}
