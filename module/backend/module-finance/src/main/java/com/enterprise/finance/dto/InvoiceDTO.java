package com.enterprise.finance.dto;

import com.enterprise.finance.entity.Invoice.InvoiceStatus;
import com.enterprise.finance.entity.Invoice.InvoiceType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @file InvoiceDTO.java
 * @description 發票回傳資料 / Invoice response DTO
 */
@Data
@Builder
public class InvoiceDTO {
    private String id;
    private String invoiceNo;
    private InvoiceType type;
    private String counterpartyId;
    private BigDecimal amount;
    private BigDecimal paidAmount;
    private LocalDate dueDate;
    private InvoiceStatus status;
}
