package com.enterprise.system.repository;

import com.enterprise.system.entity.SystemConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @file SystemConfigRepository.java
 * @description 系統設定資料存取 / System config repository
 */
@Repository
public interface SystemConfigRepository extends JpaRepository<SystemConfig, UUID> {
    Optional<SystemConfig> findByKeyAndDeletedAtIsNull(String key);

    List<SystemConfig> findByCategoryAndDeletedAtIsNullOrderByKeyAsc(String category);

    List<SystemConfig> findByDeletedAtIsNullOrderByCategoryAscKeyAsc();
}
