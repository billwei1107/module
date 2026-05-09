package com.enterprise.finance.service;

import com.enterprise.finance.dto.BudgetDTO;
import com.enterprise.finance.dto.CreateBudgetRequest;
import com.enterprise.finance.dto.RecordBudgetActualRequest;

import java.util.List;

/**
 * @file BudgetService.java
 * @description 預算服務介面 / Budget service contract
 */
public interface BudgetService {
    BudgetDTO createBudget(CreateBudgetRequest request);

    BudgetDTO recordActual(String budgetId, RecordBudgetActualRequest request);

    List<BudgetDTO> getBudgets();
}
