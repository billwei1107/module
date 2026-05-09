package com.enterprise.report.controller;

import com.enterprise.common.dto.ApiResponse;
import com.enterprise.report.dto.*;
import com.enterprise.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @file ReportController.java
 * @description 報表分析控制器 / Report analytics controller
 */
@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/definitions")
    public ApiResponse<List<ReportDefinitionDTO>> getDefinitions() {
        return ApiResponse.success(reportService.getDefinitions());
    }

    @PostMapping("/definitions")
    public ApiResponse<ReportDefinitionDTO> createDefinition(@RequestBody CreateReportDefinitionRequest request) {
        return ApiResponse.success(reportService.createDefinition(request));
    }

    @PostMapping("/definitions/{id}/execute")
    public ApiResponse<ReportExecutionResultDTO> executeReport(@PathVariable String id) {
        return ApiResponse.success(reportService.executeReport(id));
    }

    @GetMapping("/definitions/{id}/export/csv")
    public ResponseEntity<String> exportCsv(@PathVariable String id) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(reportService.exportCsv(id));
    }

    @GetMapping("/dashboards")
    public ApiResponse<List<DashboardDTO>> getDashboards() {
        return ApiResponse.success(reportService.getDashboards());
    }

    @PostMapping("/dashboards")
    public ApiResponse<DashboardDTO> createDashboard(@RequestBody CreateDashboardRequest request) {
        return ApiResponse.success(reportService.createDashboard(request));
    }

    @PostMapping("/dashboards/{dashboardId}/widgets")
    public ApiResponse<WidgetDTO> createWidget(@PathVariable String dashboardId, @RequestBody CreateWidgetRequest request) {
        return ApiResponse.success(reportService.createWidget(dashboardId, request));
    }

    @PostMapping("/schedules")
    public ApiResponse<ReportScheduleDTO> createSchedule(@RequestBody CreateReportScheduleRequest request) {
        return ApiResponse.success(reportService.createSchedule(request));
    }

    @GetMapping("/definitions/{definitionId}/schedules")
    public ApiResponse<List<ReportScheduleDTO>> getSchedules(@PathVariable String definitionId) {
        return ApiResponse.success(reportService.getSchedules(definitionId));
    }

    @GetMapping("/summary")
    public ApiResponse<BusinessSummaryDTO> getBusinessSummary() {
        return ApiResponse.success(reportService.getBusinessSummary());
    }
}
