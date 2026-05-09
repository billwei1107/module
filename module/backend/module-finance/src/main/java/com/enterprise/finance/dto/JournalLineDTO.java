package com.enterprise.finance.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @file JournalLineDTO.java
 * @description 傳票分錄回傳資料 / Journal line response DTO
 */
@Data
@Builder
public class JournalLineDTO {
    private String id;
    private String accountId;
    private BigDecimal debitAmount;
    private BigDecimal creditAmount;
    private String description;
}
