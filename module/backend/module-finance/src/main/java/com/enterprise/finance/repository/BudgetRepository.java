package com.enterprise.finance.repository;

import com.enterprise.finance.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * @file BudgetRepository.java
 * @description 預算主檔資料存取 / Budget repository
 */
@Repository
public interface BudgetRepository extends JpaRepository<Budget, UUID> {
    List<Budget> findByDeletedAtIsNullOrderByFiscalYearDescNameAsc();
}
