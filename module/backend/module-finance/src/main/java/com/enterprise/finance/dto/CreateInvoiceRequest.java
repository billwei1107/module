package com.enterprise.finance.dto;

import com.enterprise.finance.entity.Invoice.InvoiceType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @file CreateInvoiceRequest.java
 * @description 建立發票請求 / Create invoice request
 */
@Data
public class CreateInvoiceRequest {
    private String invoiceNo;
    private InvoiceType type;
    private String counterpartyId;
    private BigDecimal amount = BigDecimal.ZERO;
    private LocalDate dueDate;
}
