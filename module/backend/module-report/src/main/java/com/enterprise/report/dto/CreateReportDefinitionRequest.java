package com.enterprise.report.dto;

import lombok.Data;

/**
 * @file CreateReportDefinitionRequest.java
 * @description 建立報表定義請求 / Create report definition request
 */
@Data
public class CreateReportDefinitionRequest {
    private String name;
    private String dataSourceSql;
    private String columnsJson = "[]";
    private String filtersJson = "{}";
}
