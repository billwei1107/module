package com.enterprise.finance.service.impl;

import com.enterprise.common.annotation.Auditable;
import com.enterprise.common.exception.BusinessException;
import com.enterprise.finance.dto.BudgetDTO;
import com.enterprise.finance.dto.CreateBudgetRequest;
import com.enterprise.finance.dto.RecordBudgetActualRequest;
import com.enterprise.finance.entity.Budget;
import com.enterprise.finance.entity.BudgetItem;
import com.enterprise.finance.repository.BudgetItemRepository;
import com.enterprise.finance.repository.BudgetRepository;
import com.enterprise.finance.service.BudgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

/**
 * @file BudgetServiceImpl.java
 * @description 預算服務實作 / Budget service implementation
 * @description_en Tracks actual spending and flags budgets above the 80 percent warning threshold
 * @description_zh 追蹤實際支出並在超過 80% 預算使用率時標示警示
 */
@Service
@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService {

    private static final BigDecimal WARNING_THRESHOLD = new BigDecimal("0.80");

    private final BudgetRepository budgetRepository;
    private final BudgetItemRepository budgetItemRepository;

    @Override
    @Transactional
    @Auditable(module = "finance", action = "CREATE_BUDGET")
    public BudgetDTO createBudget(CreateBudgetRequest request) {
        Budget budget = new Budget();
        budget.setName(request.getName());
        budget.setDepartmentId(request.getDepartmentId());
        budget.setFiscalYear(request.getFiscalYear());
        budget.setTotalAmount(safeAmount(request.getTotalAmount()));
        return toDTO(budgetRepository.save(budget));
    }

    @Override
    @Transactional
    @Auditable(module = "finance", action = "RECORD_BUDGET_ACTUAL")
    public BudgetDTO recordActual(String budgetId, RecordBudgetActualRequest request) {
        Budget budget = budgetRepository.findById(UUID.fromString(budgetId))
                .orElseThrow(() -> new BusinessException(404, "預算不存在 / Budget not found"));
        UUID accountId = UUID.fromString(request.getAccountId());
        BudgetItem item = budgetItemRepository.findByBudgetIdAndAccountIdAndDeletedAtIsNull(budget.getId(), accountId)
                .orElseGet(() -> {
                    BudgetItem newItem = new BudgetItem();
                    newItem.setBudgetId(budget.getId());
                    newItem.setAccountId(accountId);
                    return newItem;
                });
        item.setActualAmount(safeAmount(item.getActualAmount()).add(safeAmount(request.getAmount())));
        budgetItemRepository.save(item);
        return toDTO(budget);
    }

    @Override
    public List<BudgetDTO> getBudgets() {
        return budgetRepository.findByDeletedAtIsNullOrderByFiscalYearDescNameAsc().stream().map(this::toDTO).toList();
    }

    private BudgetDTO toDTO(Budget budget) {
        BigDecimal actualAmount = budget.getId() == null
                ? BigDecimal.ZERO
                : budgetItemRepository.findByBudgetIdAndDeletedAtIsNull(budget.getId()).stream()
                .map(BudgetItem::getActualAmount)
                .map(this::safeAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalAmount = safeAmount(budget.getTotalAmount());
        BigDecimal usageRate = totalAmount.compareTo(BigDecimal.ZERO) == 0
                ? BigDecimal.ZERO
                : actualAmount.divide(totalAmount, 4, RoundingMode.HALF_UP);
        return BudgetDTO.builder()
                .id(budget.getId() != null ? budget.getId().toString() : null)
                .name(budget.getName())
                .departmentId(budget.getDepartmentId())
                .fiscalYear(budget.getFiscalYear())
                .totalAmount(totalAmount)
                .actualAmount(actualAmount)
                .usageRate(usageRate)
                .warning(usageRate.compareTo(WARNING_THRESHOLD) >= 0)
                .build();
    }

    private BigDecimal safeAmount(BigDecimal amount) {
        return amount == null ? BigDecimal.ZERO : amount;
    }
}
