package com.enterprise.audit.service;

import com.enterprise.audit.dto.AuditLogDTO;
import com.enterprise.audit.dto.AuditLogSearchCriteria;
import com.enterprise.audit.entity.AuditLog;
import com.enterprise.common.dto.PageResponse;
import org.springframework.data.domain.Pageable;

/**
 * @file AuditLogService.java
 * @description 稽核日誌服務介面 / Audit log service contract
 * @description_en Defines append-only write, filtered query, and CSV export operations
 * @description_zh 定義稽核紀錄寫入、多條件查詢與 CSV 匯出操作
 */
public interface AuditLogService {
    AuditLogDTO record(AuditLog auditLog);

    PageResponse<AuditLogDTO> search(AuditLogSearchCriteria criteria, Pageable pageable);

    String exportCsv(AuditLogSearchCriteria criteria);
}
