package com.enterprise.audit.entity;

import com.enterprise.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @file AuditLog.java
 * @description 稽核日誌實體 / Audit log entity
 * @description_en Stores immutable records of API access and auditable business actions
 * @description_zh 儲存 API 存取與可稽核業務行為的不可變紀錄
 */
@Entity
@Table(name = "audit_logs")
@Data
@EqualsAndHashCode(callSuper = true)
public class AuditLog extends BaseEntity {

    @Column(name = "user_id", length = 80)
    private String userId;

    @Column(name = "user_name", length = 120)
    private String userName;

    @Column(nullable = false, length = 60)
    private String module;

    @Column(nullable = false, length = 100)
    private String action;

    @Column(name = "resource_type", length = 100)
    private String resourceType;

    @Column(name = "resource_id", length = 120)
    private String resourceId;

    @Column(name = "request_method", length = 20)
    private String requestMethod;

    @Column(name = "request_url", length = 500)
    private String requestUrl;

    @Column(name = "request_body", columnDefinition = "TEXT")
    private String requestBody;

    @Column(name = "response_status")
    private Integer responseStatus;

    @Column(name = "ip_address", length = 80)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "before_data", columnDefinition = "TEXT")
    private String beforeData;

    @Column(name = "after_data", columnDefinition = "TEXT")
    private String afterData;

    @Column(name = "execution_time_ms")
    private Long executionTimeMs;

    // ========================================
    // 防篡改保護 / Tamper Protection
    // ========================================
    @PreUpdate
    @PreRemove
    public void preventMutation() {
        throw new UnsupportedOperationException("稽核日誌不可修改或刪除 / Audit logs are immutable");
    }
}
