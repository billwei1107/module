package com.enterprise.finance.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @file AgingBucketDTO.java
 * @description 帳齡區間回傳資料 / Aging bucket response DTO
 */
@Data
@Builder
public class AgingBucketDTO {
    private String bucket;
    private BigDecimal amount;
}
