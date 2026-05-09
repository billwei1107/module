package com.enterprise.payroll.repository;

import com.enterprise.payroll.entity.SalaryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @file SalaryItemRepository.java
 * @description 薪資項目資料存取 / Salary item repository
 */
@Repository
public interface SalaryItemRepository extends JpaRepository<SalaryItem, UUID> {
    Optional<SalaryItem> findByCodeAndDeletedAtIsNull(String code);

    List<SalaryItem> findByActiveTrueAndDeletedAtIsNullOrderByCodeAsc();
}
