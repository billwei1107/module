package com.enterprise.finance.service;

import com.enterprise.finance.dto.CreateExpenseClaimRequest;
import com.enterprise.finance.dto.ExpenseClaimDTO;

import java.util.List;

/**
 * @file ExpenseClaimService.java
 * @description 報銷申請服務介面 / Expense claim service contract
 */
public interface ExpenseClaimService {
    ExpenseClaimDTO submitClaim(CreateExpenseClaimRequest request);

    List<ExpenseClaimDTO> getClaims();
}
