package com.enterprise.audit.controller;

import com.enterprise.audit.dto.AuditLogDTO;
import com.enterprise.audit.dto.AuditLogSearchCriteria;
import com.enterprise.audit.service.AuditLogService;
import com.enterprise.common.dto.ApiResponse;
import com.enterprise.common.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * @file AuditLogController.java
 * @description 稽核日誌控制器 / Audit log controller
 * @description_en Provides filtered audit log search and CSV export endpoints
 * @description_zh 提供稽核日誌多條件查詢與 CSV 匯出端點
 */
@RestController
@RequestMapping("/api/v1/audit/logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    public ApiResponse<PageResponse<AuditLogDTO>> search(
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String resourceType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.success(auditLogService.search(
                criteria(module, action, userId, resourceType, startDate, endDate), pageable));
    }

    @GetMapping("/export")
    public ResponseEntity<String> exportCsv(
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String resourceType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        String csv = auditLogService.exportCsv(criteria(module, action, userId, resourceType, startDate, endDate));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=audit-logs.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }

    private AuditLogSearchCriteria criteria(
            String module,
            String action,
            String userId,
            String resourceType,
            LocalDateTime startDate,
            LocalDateTime endDate) {
        AuditLogSearchCriteria criteria = new AuditLogSearchCriteria();
        criteria.setModule(module);
        criteria.setAction(action);
        criteria.setUserId(userId);
        criteria.setResourceType(resourceType);
        criteria.setStartDate(startDate);
        criteria.setEndDate(endDate);
        return criteria;
    }
}
