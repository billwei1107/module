package com.enterprise.report.dto;

import com.enterprise.report.entity.Widget.WidgetType;
import lombok.Data;

/**
 * @file CreateWidgetRequest.java
 * @description 建立儀表板元件請求 / Create widget request
 */
@Data
public class CreateWidgetRequest {
    private String title;
    private WidgetType type = WidgetType.NUMBER;
    private String dataSourceSql;
    private String positionJson = "{}";
}
