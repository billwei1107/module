package com.enterprise.report.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @file DashboardDTO.java
 * @description 儀表板回傳資料 / Dashboard response DTO
 */
@Data
@Builder
public class DashboardDTO {
    private String id;
    private String name;
    private String ownerId;
    private String layoutJson;
    private Boolean active;
    private List<WidgetDTO> widgets;
}
