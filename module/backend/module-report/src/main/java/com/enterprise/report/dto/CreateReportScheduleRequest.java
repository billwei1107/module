package com.enterprise.report.dto;

import lombok.Data;

/**
 * @file CreateReportScheduleRequest.java
 * @description 建立報表排程請求 / Create report schedule request
 */
@Data
public class CreateReportScheduleRequest {
    private String definitionId;
    private String cronExpression;
    private String recipientEmails;
}
