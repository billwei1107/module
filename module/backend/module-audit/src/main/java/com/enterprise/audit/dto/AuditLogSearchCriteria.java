package com.enterprise.audit.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @file AuditLogSearchCriteria.java
 * @description 稽核日誌搜尋條件 / Audit log search criteria
 * @description_en Holds optional filters for audit log queries and exports
 * @description_zh 封裝稽核日誌查詢與匯出的可選篩選條件
 */
@Data
public class AuditLogSearchCriteria {
    private String module;
    private String action;
    private String userId;
    private String resourceType;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
