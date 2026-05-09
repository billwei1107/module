package com.enterprise.report.repository;

import com.enterprise.report.entity.ReportSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * @file ReportScheduleRepository.java
 * @description 報表排程資料存取 / Report schedule repository
 */
@Repository
public interface ReportScheduleRepository extends JpaRepository<ReportSchedule, UUID> {
    List<ReportSchedule> findByDefinitionIdAndDeletedAtIsNullOrderByCreatedAtDesc(UUID definitionId);
}
