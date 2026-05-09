package com.enterprise.report.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @file ReportDefinitionDTO.java
 * @description 報表定義回傳資料 / Report definition response DTO
 */
@Data
@Builder
public class ReportDefinitionDTO {
    private String id;
    private String name;
    private String dataSourceSql;
    private String columnsJson;
    private String filtersJson;
    private Boolean active;
}
