package com.enterprise.report.service.impl;

import com.enterprise.common.exception.BusinessException;
import com.enterprise.report.dto.CreateReportDefinitionRequest;
import com.enterprise.report.entity.ReportDefinition;
import com.enterprise.report.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * @file ReportServiceImplTest.java
 * @description 報表分析服務測試 / Report service tests
 */
class ReportServiceImplTest {

    @Test
    void sqlGuardShouldRejectNonSelectStatements() {
        ReportSqlGuard guard = new ReportSqlGuard();

        assertThatThrownBy(() -> guard.validateSelect("delete from fin_invoices"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("SELECT");
    }

    @Test
    void sqlGuardShouldRejectNonWhitelistedTables() {
        ReportSqlGuard guard = new ReportSqlGuard();

        assertThatThrownBy(() -> guard.validateSelect("select * from pg_user"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("未核准");
    }

    @Test
    void executeReportShouldAppendLimitAndReturnRows() {
        ReportDefinitionRepository definitionRepository = mock(ReportDefinitionRepository.class);
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        UUID definitionId = UUID.randomUUID();
        ReportDefinition definition = new ReportDefinition();
        definition.setId(definitionId);
        definition.setName("應收統計");
        definition.setDataSourceSql("select invoice_no, amount from fin_invoices");
        when(definitionRepository.findById(definitionId)).thenReturn(Optional.of(definition));
        when(jdbcTemplate.queryForList("select invoice_no, amount from fin_invoices LIMIT 500"))
                .thenReturn(List.of(Map.of("invoice_no", "INV-001", "amount", 100)));

        ReportServiceImpl service = service(definitionRepository, jdbcTemplate);

        assertThat(service.executeReport(definitionId.toString()).getRowCount()).isEqualTo(1);
        assertThat(service.executeReport(definitionId.toString()).getColumns()).contains("invoice_no", "amount");
    }

    @Test
    void createDefinitionShouldValidateSqlBeforeSaving() {
        ReportDefinitionRepository definitionRepository = mock(ReportDefinitionRepository.class);
        when(definitionRepository.save(any(ReportDefinition.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ReportServiceImpl service = service(definitionRepository, mock(JdbcTemplate.class));
        CreateReportDefinitionRequest request = new CreateReportDefinitionRequest();
        request.setName("應收統計");
        request.setDataSourceSql("select invoice_no, amount from fin_invoices");

        assertThat(service.createDefinition(request).getName()).isEqualTo("應收統計");
        verify(definitionRepository).save(any(ReportDefinition.class));
    }

    @SuppressWarnings("unchecked")
    private ReportServiceImpl service(ReportDefinitionRepository definitionRepository, JdbcTemplate jdbcTemplate) {
        return new ReportServiceImpl(
                definitionRepository,
                mock(ReportScheduleRepository.class),
                mock(DashboardConfigRepository.class),
                mock(WidgetRepository.class),
                jdbcTemplate,
                new ReportSqlGuard());
    }
}
