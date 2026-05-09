package com.enterprise.payroll.repository;

import com.enterprise.payroll.entity.TaxConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * @file TaxConfigRepository.java
 * @description 所得稅級距資料存取 / Tax config repository
 */
@Repository
public interface TaxConfigRepository extends JpaRepository<TaxConfig, UUID> {
    List<TaxConfig> findByDeletedAtIsNullOrderByBracketStartAsc();
}
