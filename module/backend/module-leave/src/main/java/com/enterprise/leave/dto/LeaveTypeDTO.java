package com.enterprise.leave.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @file LeaveTypeDTO.java
 * @description 假別設定回傳 / Leave type response DTO
 */
@Data
@Builder
public class LeaveTypeDTO {
    private String id;
    private String name;
    private String code;
    private Integer annualQuotaHours;
    private Boolean requiresApproval;
    private Boolean paid;
    private Boolean active;
}
