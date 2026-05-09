package com.enterprise.report.service;

import com.enterprise.report.dto.*;

import java.util.List;

/**
 * @file ReportService.java
 * @description 報表分析服務介面 / Report service contract
 */
public interface ReportService {
    ReportDefinitionDTO createDefinition(CreateReportDefinitionRequest request);

    List<ReportDefinitionDTO> getDefinitions();

    ReportExecutionResultDTO executeReport(String definitionId);

    String exportCsv(String definitionId);

    DashboardDTO createDashboard(CreateDashboardRequest request);

    List<DashboardDTO> getDashboards();

    WidgetDTO createWidget(String dashboardId, CreateWidgetRequest request);

    ReportScheduleDTO createSchedule(CreateReportScheduleRequest request);

    List<ReportScheduleDTO> getSchedules(String definitionId);

    BusinessSummaryDTO getBusinessSummary();
}
