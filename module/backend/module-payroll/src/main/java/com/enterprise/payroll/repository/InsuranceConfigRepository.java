package com.enterprise.payroll.repository;

import com.enterprise.payroll.entity.InsuranceConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @file InsuranceConfigRepository.java
 * @description 勞健保設定資料存取 / Insurance config repository
 */
@Repository
public interface InsuranceConfigRepository extends JpaRepository<InsuranceConfig, UUID> {
    List<InsuranceConfig> findByDeletedAtIsNullOrderByTypeAsc();

    Optional<InsuranceConfig> findByTypeAndDeletedAtIsNull(InsuranceConfig.InsuranceType type);
}
