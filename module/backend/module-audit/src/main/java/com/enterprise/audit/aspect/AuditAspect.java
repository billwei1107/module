package com.enterprise.audit.aspect;

import com.enterprise.audit.entity.AuditLog;
import com.enterprise.audit.service.AuditLogService;
import com.enterprise.common.annotation.Auditable;
import com.enterprise.common.security.SecurityUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @file AuditAspect.java
 * @description 稽核日誌 AOP 切面 / Audit logging aspect
 * @description_en Records methods annotated with @Auditable without coupling business modules to audit persistence
 * @description_zh 攔截標記 @Auditable 的方法，避免業務模組直接依賴稽核儲存邏輯
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditLogService auditLogService;
    private final ObjectMapper objectMapper;

    @Around("@annotation(auditable)")
    public Object recordAuditLog(ProceedingJoinPoint joinPoint, Auditable auditable) throws Throwable {
        long startedAt = System.currentTimeMillis();
        Object result = null;
        Throwable failure = null;
        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable throwable) {
            failure = throwable;
            throw throwable;
        } finally {
            writeAuditLog(joinPoint, auditable, result, failure, System.currentTimeMillis() - startedAt);
        }
    }

    // ========================================
    // 日誌寫入 / Audit Record Creation
    // ========================================
    private void writeAuditLog(
            ProceedingJoinPoint joinPoint,
            Auditable auditable,
            Object result,
            Throwable failure,
            long executionTimeMs) {
        try {
            AuditLog log = new AuditLog();
            log.setUserId(SecurityUtils.getCurrentUserId());
            log.setUserName(SecurityUtils.getCurrentUserId());
            log.setModule(resolveModule(joinPoint, auditable));
            log.setAction(resolveAction(joinPoint, auditable));
            log.setResourceType(joinPoint.getSignature().getDeclaringType().getSimpleName());
            log.setRequestBody(toJson(joinPoint.getArgs()));
            log.setAfterData(failure == null ? toJson(result) : toJson(failure.getMessage()));
            log.setResponseStatus(failure == null ? 200 : 500);
            log.setExecutionTimeMs(executionTimeMs);
            enrichHttpRequest(log);
            auditLogService.record(log);
        } catch (Exception exception) {
            log.warn("Failed to write audit log", exception);
        }
    }

    private String resolveModule(ProceedingJoinPoint joinPoint, Auditable auditable) {
        if (auditable.module() != null && !auditable.module().isBlank()) {
            return auditable.module();
        }
        String packageName = joinPoint.getSignature().getDeclaringType().getPackageName();
        String[] parts = packageName.split("\\.");
        return parts.length >= 3 ? parts[2] : "system";
    }

    private String resolveAction(ProceedingJoinPoint joinPoint, Auditable auditable) {
        if (auditable.action() != null && !auditable.action().isBlank()) {
            return auditable.action();
        }
        return joinPoint.getSignature().getName();
    }

    private void enrichHttpRequest(AuditLog log) {
        if (!(RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes)) {
            return;
        }
        HttpServletRequest request = attributes.getRequest();
        log.setRequestMethod(request.getMethod());
        log.setRequestUrl(request.getRequestURI());
        log.setIpAddress(resolveClientIp(request));
        log.setUserAgent(request.getHeader("User-Agent"));
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String toJson(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            return String.valueOf(value);
        }
    }
}
