package com.enterprise.finance.repository;

import com.enterprise.finance.entity.BudgetItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @file BudgetItemRepository.java
 * @description 預算明細資料存取 / Budget item repository
 */
@Repository
public interface BudgetItemRepository extends JpaRepository<BudgetItem, UUID> {
    List<BudgetItem> findByBudgetIdAndDeletedAtIsNull(UUID budgetId);

    Optional<BudgetItem> findByBudgetIdAndAccountIdAndDeletedAtIsNull(UUID budgetId, UUID accountId);
}
