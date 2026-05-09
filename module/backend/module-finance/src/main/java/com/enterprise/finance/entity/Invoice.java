package com.enterprise.finance.entity;

import com.enterprise.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @file Invoice.java
 * @description 發票應收應付實體 / Invoice entity
 * @description_en Tracks receivable and payable invoices with payment status
 * @description_zh 追蹤應收與應付發票的金額、付款與到期狀態
 */
@Entity
@Table(name = "fin_invoices")
@Data
@EqualsAndHashCode(callSuper = true)
public class Invoice extends BaseEntity {

    public enum InvoiceType {
        RECEIVABLE, PAYABLE
    }

    public enum InvoiceStatus {
        DRAFT, ISSUED, PAID, OVERDUE
    }

    @Column(name = "invoice_no", nullable = false, unique = true, length = 50)
    private String invoiceNo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InvoiceType type;

    @Column(name = "counterparty_id", length = 80)
    private String counterpartyId;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount = BigDecimal.ZERO;

    @Column(name = "paid_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal paidAmount = BigDecimal.ZERO;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InvoiceStatus status = InvoiceStatus.DRAFT;
}
