package com.enterprise.report.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @file ReportExecutionResultDTO.java
 * @description 報表執行結果 / Report execution result DTO
 */
@Data
@Builder
public class ReportExecutionResultDTO {
    private String definitionId;
    private List<String> columns;
    private List<Map<String, Object>> rows;
    private Integer rowCount;
}
