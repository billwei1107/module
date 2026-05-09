package com.enterprise.audit.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @file AuditLogDTO.java
 * @description 稽核日誌回傳資料 / Audit log response DTO
 * @description_en Represents an immutable audit record returned to clients
 * @description_zh 封裝回傳給前端的不可變稽核紀錄
 */
@Data
@Builder
public class AuditLogDTO {
    private String id;
    private String userId;
    private String userName;
    private String module;
    private String action;
    private String resourceType;
    private String resourceId;
    private String requestMethod;
    private String requestUrl;
    private String requestBody;
    private Integer responseStatus;
    private String ipAddress;
    private String userAgent;
    private String beforeData;
    private String afterData;
    private Long executionTimeMs;
    private LocalDateTime createdAt;
}
