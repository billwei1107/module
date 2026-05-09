package com.enterprise.finance.service.impl;

import com.enterprise.common.annotation.Auditable;
import com.enterprise.finance.dto.CreateExpenseClaimRequest;
import com.enterprise.finance.dto.ExpenseClaimDTO;
import com.enterprise.finance.entity.ExpenseClaim;
import com.enterprise.finance.entity.ExpenseClaim.ExpenseClaimStatus;
import com.enterprise.finance.repository.ExpenseClaimRepository;
import com.enterprise.finance.service.ExpenseClaimService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * @file ExpenseClaimServiceImpl.java
 * @description 報銷申請服務實作 / Expense claim service implementation
 * @description_en Stores reimbursement requests and leaves workflow integration as an optional module extension
 * @description_zh 儲存報銷申請，審批流程整合保留為可選模組延伸
 */
@Service
@RequiredArgsConstructor
public class ExpenseClaimServiceImpl implements ExpenseClaimService {

    private final ExpenseClaimRepository expenseClaimRepository;

    @Override
    @Transactional
    @Auditable(module = "finance", action = "SUBMIT_EXPENSE_CLAIM")
    public ExpenseClaimDTO submitClaim(CreateExpenseClaimRequest request) {
        ExpenseClaim claim = new ExpenseClaim();
        claim.setEmployeeId(request.getEmployeeId());
        claim.setAmount(request.getAmount() == null ? BigDecimal.ZERO : request.getAmount());
        claim.setCategory(request.getCategory());
        claim.setDescription(request.getDescription());
        claim.setStatus(ExpenseClaimStatus.PENDING);
        return toDTO(expenseClaimRepository.save(claim));
    }

    @Override
    public List<ExpenseClaimDTO> getClaims() {
        return expenseClaimRepository.findByDeletedAtIsNullOrderByCreatedAtDesc().stream().map(this::toDTO).toList();
    }

    private ExpenseClaimDTO toDTO(ExpenseClaim claim) {
        return ExpenseClaimDTO.builder()
                .id(claim.getId() != null ? claim.getId().toString() : null)
                .employeeId(claim.getEmployeeId())
                .amount(claim.getAmount())
                .category(claim.getCategory())
                .description(claim.getDescription())
                .status(claim.getStatus())
                .workflowInstanceId(claim.getWorkflowInstanceId())
                .build();
    }
}
