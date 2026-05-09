package com.enterprise.finance.repository;

import com.enterprise.finance.entity.ExpenseClaim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * @file ExpenseClaimRepository.java
 * @description 報銷申請資料存取 / Expense claim repository
 */
@Repository
public interface ExpenseClaimRepository extends JpaRepository<ExpenseClaim, UUID> {
    List<ExpenseClaim> findByDeletedAtIsNullOrderByCreatedAtDesc();
}
