package com.enterprise.leave.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @file CreateLeaveRequest.java
 * @description 建立請假申請請求 / Create leave request payload
 */
@Data
public class CreateLeaveRequest {
    private String employeeId;
    private String leaveTypeId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String reason;
}
