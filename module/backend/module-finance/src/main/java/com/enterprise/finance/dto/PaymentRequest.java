package com.enterprise.finance.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @file PaymentRequest.java
 * @description 收付款請求 / Payment request
 */
@Data
public class PaymentRequest {
    private BigDecimal amount = BigDecimal.ZERO;
    private LocalDate paymentDate;
    private String paymentMethod;
    private String referenceNo;
}
