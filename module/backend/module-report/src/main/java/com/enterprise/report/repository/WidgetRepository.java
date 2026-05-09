package com.enterprise.report.repository;

import com.enterprise.report.entity.Widget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * @file WidgetRepository.java
 * @description 儀表板元件資料存取 / Widget repository
 */
@Repository
public interface WidgetRepository extends JpaRepository<Widget, UUID> {
    List<Widget> findByDashboardIdAndDeletedAtIsNullOrderByCreatedAtAsc(UUID dashboardId);
}
