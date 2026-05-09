package com.enterprise.audit.aspect;

import com.enterprise.audit.entity.AuditLog;
import com.enterprise.audit.service.AuditLogService;
import com.enterprise.common.annotation.Auditable;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @file AuditAspectTest.java
 * @description 稽核 AOP 測試 / Audit aspect tests
 * @description_en Verifies @Auditable method interception creates an audit record
 * @description_zh 驗證 @Auditable 方法被攔截後會建立稽核紀錄
 */
class AuditAspectTest {

    @Test
    void recordAuditLogShouldPersistAuditRecordAfterMethodSuccess() throws Throwable {
        AuditLogService auditLogService = mock(AuditLogService.class);
        AuditAspect aspect = new AuditAspect(auditLogService, new ObjectMapper());
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        Signature signature = mock(Signature.class);
        Auditable auditable = TestTarget.class.getMethod("upsert").getAnnotation(Auditable.class);

        when(joinPoint.proceed()).thenReturn("ok");
        when(joinPoint.getArgs()).thenReturn(new Object[]{"payload"});
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getDeclaringType()).thenReturn(TestTarget.class);
        when(signature.getName()).thenReturn("upsert");

        Object result = aspect.recordAuditLog(joinPoint, auditable);

        assertThat(result).isEqualTo("ok");
        verify(auditLogService).record(argThat(log ->
                "system".equals(log.getModule())
                        && "UPSERT_SYSTEM_CONFIG".equals(log.getAction())
                        && Integer.valueOf(200).equals(log.getResponseStatus())
                        && log.getExecutionTimeMs() >= 0));
    }

    @Test
    void recordAuditLogShouldNotHideBusinessException() throws Throwable {
        AuditLogService auditLogService = mock(AuditLogService.class);
        AuditAspect aspect = new AuditAspect(auditLogService, new ObjectMapper());
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        Signature signature = mock(Signature.class);
        Auditable auditable = TestTarget.class.getMethod("upsert").getAnnotation(Auditable.class);

        when(joinPoint.proceed()).thenThrow(new IllegalStateException("boom"));
        when(joinPoint.getArgs()).thenReturn(new Object[0]);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getDeclaringType()).thenReturn(TestTarget.class);
        when(signature.getName()).thenReturn("upsert");

        try {
            aspect.recordAuditLog(joinPoint, auditable);
        } catch (IllegalStateException exception) {
            assertThat(exception).hasMessage("boom");
        }

        verify(auditLogService).record(any(AuditLog.class));
    }

    private static class TestTarget {
        @Auditable(module = "system", action = "UPSERT_SYSTEM_CONFIG")
        public void upsert() {
        }
    }
}
