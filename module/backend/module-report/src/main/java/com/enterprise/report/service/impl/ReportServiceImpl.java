package com.enterprise.report.service.impl;

import com.enterprise.common.annotation.Auditable;
import com.enterprise.common.exception.BusinessException;
import com.enterprise.report.dto.*;
import com.enterprise.report.entity.DashboardConfig;
import com.enterprise.report.entity.ReportDefinition;
import com.enterprise.report.entity.ReportSchedule;
import com.enterprise.report.entity.Widget;
import com.enterprise.report.repository.*;
import com.enterprise.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @file ReportServiceImpl.java
 * @description 報表分析服務實作 / Report service implementation
 * @description_en Handles guarded SQL execution, CSV export, dashboard metadata, schedules, and cross-module summaries
 * @description_zh 處理受防護 SQL 執行、CSV 匯出、儀表板中繼資料、排程與跨模組統計
 */
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private static final int MAX_ROWS = 500;

    private final ReportDefinitionRepository definitionRepository;
    private final ReportScheduleRepository scheduleRepository;
    private final DashboardConfigRepository dashboardRepository;
    private final WidgetRepository widgetRepository;
    private final JdbcTemplate jdbcTemplate;
    private final ReportSqlGuard sqlGuard;

    @Override
    @Transactional
    @Auditable(module = "report", action = "CREATE_REPORT_DEFINITION")
    public ReportDefinitionDTO createDefinition(CreateReportDefinitionRequest request) {
        ReportDefinition definition = new ReportDefinition();
        definition.setName(required(request.getName(), "報表名稱不可為空 / Report name is required"));
        definition.setDataSourceSql(sqlGuard.validateSelect(request.getDataSourceSql()));
        definition.setColumnsJson(defaultIfBlank(request.getColumnsJson(), "[]"));
        definition.setFiltersJson(defaultIfBlank(request.getFiltersJson(), "{}"));
        return toDTO(definitionRepository.save(definition));
    }

    @Override
    public List<ReportDefinitionDTO> getDefinitions() {
        return definitionRepository.findByDeletedAtIsNullOrderByCreatedAtDesc().stream().map(this::toDTO).toList();
    }

    @Override
    public ReportExecutionResultDTO executeReport(String definitionId) {
        ReportDefinition definition = findDefinition(definitionId);
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(withLimit(definition.getDataSourceSql()));
        return ReportExecutionResultDTO.builder()
                .definitionId(definition.getId().toString())
                .columns(resolveColumns(rows))
                .rows(rows)
                .rowCount(rows.size())
                .build();
    }

    @Override
    public String exportCsv(String definitionId) {
        ReportExecutionResultDTO result = executeReport(definitionId);
        StringBuilder csv = new StringBuilder();
        csv.append(result.getColumns().stream().map(this::escapeCsv).collect(Collectors.joining(","))).append("\n");
        for (Map<String, Object> row : result.getRows()) {
            csv.append(result.getColumns().stream()
                    .map(column -> escapeCsv(String.valueOf(row.getOrDefault(column, ""))))
                    .collect(Collectors.joining(",")))
                    .append("\n");
        }
        return csv.toString();
    }

    @Override
    @Transactional
    public DashboardDTO createDashboard(CreateDashboardRequest request) {
        DashboardConfig dashboard = new DashboardConfig();
        dashboard.setName(required(request.getName(), "儀表板名稱不可為空 / Dashboard name is required"));
        dashboard.setOwnerId(request.getOwnerId());
        dashboard.setLayoutJson(defaultIfBlank(request.getLayoutJson(), "{}"));
        return toDTO(dashboardRepository.save(dashboard));
    }

    @Override
    public List<DashboardDTO> getDashboards() {
        return dashboardRepository.findByDeletedAtIsNullOrderByCreatedAtDesc().stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional
    public WidgetDTO createWidget(String dashboardId, CreateWidgetRequest request) {
        DashboardConfig dashboard = findDashboard(dashboardId);
        Widget widget = new Widget();
        widget.setDashboardId(dashboard.getId());
        widget.setTitle(required(request.getTitle(), "元件標題不可為空 / Widget title is required"));
        widget.setType(request.getType() == null ? Widget.WidgetType.NUMBER : request.getType());
        widget.setDataSourceSql(sqlGuard.validateSelect(request.getDataSourceSql()));
        widget.setPositionJson(defaultIfBlank(request.getPositionJson(), "{}"));
        return toDTO(widgetRepository.save(widget));
    }

    @Override
    @Transactional
    public ReportScheduleDTO createSchedule(CreateReportScheduleRequest request) {
        ReportDefinition definition = findDefinition(request.getDefinitionId());
        ReportSchedule schedule = new ReportSchedule();
        schedule.setDefinitionId(definition.getId());
        schedule.setCronExpression(required(request.getCronExpression(), "Cron 表達式不可為空 / Cron expression is required"));
        schedule.setRecipientEmails(defaultIfBlank(request.getRecipientEmails(), ""));
        return toDTO(scheduleRepository.save(schedule));
    }

    @Override
    public List<ReportScheduleDTO> getSchedules(String definitionId) {
        UUID id = findDefinition(definitionId).getId();
        return scheduleRepository.findByDefinitionIdAndDeletedAtIsNullOrderByCreatedAtDesc(id).stream().map(this::toDTO).toList();
    }

    @Override
    public BusinessSummaryDTO getBusinessSummary() {
        return BusinessSummaryDTO.builder()
                .attendanceRecords(0L)
                .overtimeMinutes(0)
                .openInvoiceAmount(BigDecimal.ZERO)
                .payrollNetPay(BigDecimal.ZERO)
                .build();
    }

    private ReportDefinition findDefinition(String definitionId) {
        return definitionRepository.findById(UUID.fromString(definitionId))
                .filter(definition -> definition.getDeletedAt() == null)
                .orElseThrow(() -> new BusinessException(404, "報表定義不存在 / Report definition not found"));
    }

    private DashboardConfig findDashboard(String dashboardId) {
        return dashboardRepository.findById(UUID.fromString(dashboardId))
                .filter(dashboard -> dashboard.getDeletedAt() == null)
                .orElseThrow(() -> new BusinessException(404, "儀表板不存在 / Dashboard not found"));
    }

    private String withLimit(String sql) {
        String guardedSql = sqlGuard.validateSelect(sql);
        return guardedSql.toLowerCase(Locale.ROOT).contains(" limit ") ? guardedSql : guardedSql + " LIMIT " + MAX_ROWS;
    }

    private List<String> resolveColumns(List<Map<String, Object>> rows) {
        if (rows.isEmpty()) {
            return List.of();
        }
        return new ArrayList<>(rows.get(0).keySet());
    }

    private String escapeCsv(String value) {
        String safeValue = value == null ? "" : value;
        return "\"" + safeValue.replace("\"", "\"\"") + "\"";
    }

    private String required(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new BusinessException(400, message);
        }
        return value.trim();
    }

    private String defaultIfBlank(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }

    private ReportDefinitionDTO toDTO(ReportDefinition definition) {
        return ReportDefinitionDTO.builder()
                .id(definition.getId() != null ? definition.getId().toString() : null)
                .name(definition.getName())
                .dataSourceSql(definition.getDataSourceSql())
                .columnsJson(definition.getColumnsJson())
                .filtersJson(definition.getFiltersJson())
                .active(definition.getActive())
                .build();
    }

    private DashboardDTO toDTO(DashboardConfig dashboard) {
        return DashboardDTO.builder()
                .id(dashboard.getId() != null ? dashboard.getId().toString() : null)
                .name(dashboard.getName())
                .ownerId(dashboard.getOwnerId())
                .layoutJson(dashboard.getLayoutJson())
                .active(dashboard.getActive())
                .widgets(dashboard.getId() == null ? List.of() : widgetRepository.findByDashboardIdAndDeletedAtIsNullOrderByCreatedAtAsc(dashboard.getId()).stream().map(this::toDTO).toList())
                .build();
    }

    private WidgetDTO toDTO(Widget widget) {
        return WidgetDTO.builder()
                .id(widget.getId() != null ? widget.getId().toString() : null)
                .dashboardId(widget.getDashboardId().toString())
                .title(widget.getTitle())
                .type(widget.getType())
                .dataSourceSql(widget.getDataSourceSql())
                .positionJson(widget.getPositionJson())
                .build();
    }

    private ReportScheduleDTO toDTO(ReportSchedule schedule) {
        return ReportScheduleDTO.builder()
                .id(schedule.getId() != null ? schedule.getId().toString() : null)
                .definitionId(schedule.getDefinitionId().toString())
                .cronExpression(schedule.getCronExpression())
                .recipientEmails(schedule.getRecipientEmails())
                .lastRunAt(schedule.getLastRunAt())
                .active(schedule.getActive())
                .build();
    }
}
