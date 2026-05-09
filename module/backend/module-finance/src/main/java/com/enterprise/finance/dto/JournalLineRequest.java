package com.enterprise.finance.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @file JournalLineRequest.java
 * @description 傳票分錄請求 / Journal line request
 */
@Data
public class JournalLineRequest {
    private String accountId;
    private BigDecimal debitAmount = BigDecimal.ZERO;
    private BigDecimal creditAmount = BigDecimal.ZERO;
    private String description;
}
