package com.enterprise.report.dto;

import com.enterprise.report.entity.Widget.WidgetType;
import lombok.Builder;
import lombok.Data;

/**
 * @file WidgetDTO.java
 * @description 儀表板元件回傳資料 / Widget response DTO
 */
@Data
@Builder
public class WidgetDTO {
    private String id;
    private String dashboardId;
    private String title;
    private WidgetType type;
    private String dataSourceSql;
    private String positionJson;
}
