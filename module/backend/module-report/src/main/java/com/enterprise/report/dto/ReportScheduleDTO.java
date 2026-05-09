package com.enterprise.report.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @file ReportScheduleDTO.java
 * @description 報表排程回傳資料 / Report schedule response DTO
 */
@Data
@Builder
public class ReportScheduleDTO {
    private String id;
    private String definitionId;
    private String cronExpression;
    private String recipientEmails;
    private LocalDateTime lastRunAt;
    private Boolean active;
}
