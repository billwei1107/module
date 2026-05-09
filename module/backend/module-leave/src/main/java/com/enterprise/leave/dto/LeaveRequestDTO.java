package com.enterprise.leave.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @file LeaveRequestDTO.java
 * @description 請假申請回傳 / Leave request response DTO
 */
@Data
@Builder
public class LeaveRequestDTO {
    private String id;
    private String employeeId;
    private String leaveTypeId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer hours;
    private String reason;
    private String status;
    private String workflowInstanceId;
}
