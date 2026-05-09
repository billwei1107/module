package com.enterprise.report.repository;

import com.enterprise.report.entity.ReportDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * @file ReportDefinitionRepository.java
 * @description 報表定義資料存取 / Report definition repository
 */
@Repository
public interface ReportDefinitionRepository extends JpaRepository<ReportDefinition, UUID> {
    List<ReportDefinition> findByDeletedAtIsNullOrderByCreatedAtDesc();
}
