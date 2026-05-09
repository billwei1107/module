package com.enterprise.project.dto;

import lombok.Data;

/**
 * @file RecordTimeLogRequest.java
 * @description 記錄工時請求 / Record time log request
 */
@Data
public class RecordTimeLogRequest {
    private String taskId;
    private String employeeId;
    private Integer minutes;
    private String note;
}
