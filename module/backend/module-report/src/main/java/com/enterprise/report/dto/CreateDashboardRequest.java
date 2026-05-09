package com.enterprise.report.dto;

import lombok.Data;

/**
 * @file CreateDashboardRequest.java
 * @description 建立儀表板請求 / Create dashboard request
 */
@Data
public class CreateDashboardRequest {
    private String name;
    private String ownerId;
    private String layoutJson = "{}";
}
