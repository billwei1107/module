package com.enterprise.report.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @file BusinessSummaryDTO.java
 * @description 跨模組統計回傳資料 / Cross-module business summary DTO
 */
@Data
@Builder
public class BusinessSummaryDTO {
    private Long attendanceRecords;
    private Integer overtimeMinutes;
    private BigDecimal openInvoiceAmount;
    private BigDecimal payrollNetPay;
}
