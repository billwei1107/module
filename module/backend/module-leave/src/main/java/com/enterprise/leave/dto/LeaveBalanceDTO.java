package com.enterprise.leave.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @file LeaveBalanceDTO.java
 * @description 請假配額回傳 / Leave balance response DTO
 */
@Data
@Builder
public class LeaveBalanceDTO {
    private String id;
    private String employeeId;
    private String leaveTypeId;
    private Integer year;
    private Integer totalHours;
    private Integer usedHours;
    private Integer reservedHours;
    private Integer availableHours;
}
