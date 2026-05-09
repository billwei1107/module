package com.enterprise.audit.repository;

import com.enterprise.audit.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * @file AuditLogRepository.java
 * @description 稽核日誌資料存取 / Audit log repository
 * @description_en Provides append-only persistence and read queries for audit records
 * @description_zh 提供稽核紀錄寫入與查詢，刪改由實體生命週期保護阻擋
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID>, JpaSpecificationExecutor<AuditLog> {
}
