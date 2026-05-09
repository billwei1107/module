package com.enterprise.audit.service.impl;

import com.enterprise.audit.dto.AuditLogDTO;
import com.enterprise.audit.dto.AuditLogSearchCriteria;
import com.enterprise.audit.entity.AuditLog;
import com.enterprise.audit.repository.AuditLogRepository;
import com.enterprise.audit.service.AuditLogService;
import com.enterprise.common.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @file AuditLogServiceImpl.java
 * @description 稽核日誌服務實作 / Audit log service implementation
 * @description_en Handles immutable audit writes, filtered reads, and CSV export
 * @description_zh 處理不可變稽核紀錄寫入、條件查詢與 CSV 匯出
 */
@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Override
    @Transactional
    public AuditLogDTO record(AuditLog auditLog) {
        return toDTO(auditLogRepository.save(auditLog));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AuditLogDTO> search(AuditLogSearchCriteria criteria, Pageable pageable) {
        Page<AuditLogDTO> page = auditLogRepository.findAll(toSpecification(criteria), pageable).map(this::toDTO);
        return PageResponse.of(page);
    }

    @Override
    @Transactional(readOnly = true)
    public String exportCsv(AuditLogSearchCriteria criteria) {
        List<AuditLogDTO> logs = auditLogRepository.findAll(toSpecification(criteria)).stream()
                .map(this::toDTO)
                .toList();
        StringBuilder csv = new StringBuilder();
        csv.append("createdAt,userId,userName,module,action,resourceType,resourceId,requestMethod,requestUrl,responseStatus,ipAddress,executionTimeMs\n");
        logs.forEach(log -> csv.append(csvValue(log.getCreatedAt()))
                .append(',').append(csvValue(log.getUserId()))
                .append(',').append(csvValue(log.getUserName()))
                .append(',').append(csvValue(log.getModule()))
                .append(',').append(csvValue(log.getAction()))
                .append(',').append(csvValue(log.getResourceType()))
                .append(',').append(csvValue(log.getResourceId()))
                .append(',').append(csvValue(log.getRequestMethod()))
                .append(',').append(csvValue(log.getRequestUrl()))
                .append(',').append(csvValue(log.getResponseStatus()))
                .append(',').append(csvValue(log.getIpAddress()))
                .append(',').append(csvValue(log.getExecutionTimeMs()))
                .append('\n'));
        return csv.toString();
    }

    // ========================================
    // 查詢條件組裝 / Query Specification
    // ========================================
    private Specification<AuditLog> toSpecification(AuditLogSearchCriteria criteria) {
        return (root, query, cb) -> {
            Specification<AuditLog> spec = Specification.where(null);
            if (criteria == null) {
                return spec.toPredicate(root, query, cb);
            }
            if (criteria.getModule() != null && !criteria.getModule().isBlank()) {
                spec = spec.and((r, q, c) -> c.equal(r.get("module"), criteria.getModule()));
            }
            if (criteria.getAction() != null && !criteria.getAction().isBlank()) {
                spec = spec.and((r, q, c) -> c.equal(r.get("action"), criteria.getAction()));
            }
            if (criteria.getUserId() != null && !criteria.getUserId().isBlank()) {
                spec = spec.and((r, q, c) -> c.equal(r.get("userId"), criteria.getUserId()));
            }
            if (criteria.getResourceType() != null && !criteria.getResourceType().isBlank()) {
                spec = spec.and((r, q, c) -> c.equal(r.get("resourceType"), criteria.getResourceType()));
            }
            LocalDateTime startDate = criteria.getStartDate();
            LocalDateTime endDate = criteria.getEndDate();
            if (startDate != null) {
                spec = spec.and((r, q, c) -> c.greaterThanOrEqualTo(r.get("createdAt"), startDate));
            }
            if (endDate != null) {
                spec = spec.and((r, q, c) -> c.lessThanOrEqualTo(r.get("createdAt"), endDate));
            }
            return spec.toPredicate(root, query, cb);
        };
    }

    private AuditLogDTO toDTO(AuditLog log) {
        return AuditLogDTO.builder()
                .id(log.getId() != null ? log.getId().toString() : null)
                .userId(log.getUserId())
                .userName(log.getUserName())
                .module(log.getModule())
                .action(log.getAction())
                .resourceType(log.getResourceType())
                .resourceId(log.getResourceId())
                .requestMethod(log.getRequestMethod())
                .requestUrl(log.getRequestUrl())
                .requestBody(log.getRequestBody())
                .responseStatus(log.getResponseStatus())
                .ipAddress(log.getIpAddress())
                .userAgent(log.getUserAgent())
                .beforeData(log.getBeforeData())
                .afterData(log.getAfterData())
                .executionTimeMs(log.getExecutionTimeMs())
                .createdAt(log.getCreatedAt())
                .build();
    }

    private String csvValue(Object value) {
        if (value == null) {
            return "";
        }
        String text = String.valueOf(value);
        if (text.contains(",") || text.contains("\"") || text.contains("\n")) {
            return '"' + text.replace("\"", "\"\"") + '"';
        }
        return text;
    }
}
