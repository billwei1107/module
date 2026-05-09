package com.enterprise.audit.service.impl;

import com.enterprise.audit.dto.AuditLogSearchCriteria;
import com.enterprise.audit.entity.AuditLog;
import com.enterprise.audit.repository.AuditLogRepository;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @file AuditLogServiceImplTest.java
 * @description 稽核日誌服務測試 / Audit log service tests
 * @description_en Verifies audit log recording and CSV export formatting
 * @description_zh 驗證稽核紀錄寫入與 CSV 匯出格式
 */
class AuditLogServiceImplTest {

    @Test
    void recordShouldPersistAndReturnDto() {
        AuditLogRepository repository = mock(AuditLogRepository.class);
        AuditLog log = new AuditLog();
        log.setModule("system");
        log.setAction("UPSERT_SYSTEM_CONFIG");

        when(repository.save(log)).thenReturn(log);

        AuditLogServiceImpl service = new AuditLogServiceImpl(repository);

        assertThat(service.record(log).getModule()).isEqualTo("system");
    }

    @Test
    void exportCsvShouldEscapeCommaValues() {
        AuditLogRepository repository = mock(AuditLogRepository.class);
        AuditLog log = new AuditLog();
        log.setModule("system");
        log.setAction("UPDATE,CONFIG");
        log.setUserId("user-001");
        log.setCreatedAt(LocalDateTime.of(2026, 5, 9, 17, 0));

        when(repository.findAll(org.mockito.ArgumentMatchers.<Specification<AuditLog>>any())).thenReturn(List.of(log));

        AuditLogServiceImpl service = new AuditLogServiceImpl(repository);
        String csv = service.exportCsv(new AuditLogSearchCriteria());

        assertThat(csv).contains("createdAt,userId,userName,module,action");
        assertThat(csv).contains("\"UPDATE,CONFIG\"");
    }
}
