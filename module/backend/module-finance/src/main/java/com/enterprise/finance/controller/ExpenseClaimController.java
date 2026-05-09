package com.enterprise.finance.controller;

import com.enterprise.common.dto.ApiResponse;
import com.enterprise.common.security.SecurityUtils;
import com.enterprise.finance.dto.CreateExpenseClaimRequest;
import com.enterprise.finance.dto.ExpenseClaimDTO;
import com.enterprise.finance.service.ExpenseClaimService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @file ExpenseClaimController.java
 * @description 報銷申請控制器 / Expense claim controller
 */
@RestController
@RequestMapping("/api/v1/finance/expense-claims")
@RequiredArgsConstructor
public class ExpenseClaimController {

    private final ExpenseClaimService expenseClaimService;

    @GetMapping
    public ApiResponse<List<ExpenseClaimDTO>> getClaims() {
        return ApiResponse.success(expenseClaimService.getClaims());
    }

    @PostMapping
    public ApiResponse<ExpenseClaimDTO> submitClaim(@RequestBody CreateExpenseClaimRequest request) {
        if (request.getEmployeeId() == null || request.getEmployeeId().isBlank()) {
            request.setEmployeeId(SecurityUtils.getCurrentUserId());
        }
        return ApiResponse.success(expenseClaimService.submitClaim(request));
    }
}
