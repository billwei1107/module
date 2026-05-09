package com.enterprise.report.repository;

import com.enterprise.report.entity.DashboardConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * @file DashboardConfigRepository.java
 * @description 儀表板配置資料存取 / Dashboard config repository
 */
@Repository
public interface DashboardConfigRepository extends JpaRepository<DashboardConfig, UUID> {
    List<DashboardConfig> findByDeletedAtIsNullOrderByCreatedAtDesc();
}
