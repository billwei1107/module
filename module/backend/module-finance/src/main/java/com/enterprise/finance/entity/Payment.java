package com.enterprise.finance.entity;

import com.enterprise.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * @file Payment.java
 * @description 收付款實體 / Payment entity
 * @description_en Stores payments linked to receivable or payable invoices
 * @description_zh 儲存連結至應收或應付發票的收付款紀錄
 */
@Entity
@Table(name = "fin_payments")
@Data
@EqualsAndHashCode(callSuper = true)
public class Payment extends BaseEntity {

    @Column(name = "invoice_id", nullable = false)
    private UUID invoiceId;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount = BigDecimal.ZERO;

    @Column(name = "payment_date", nullable = false)
    private LocalDate paymentDate;

    @Column(name = "payment_method", nullable = false, length = 50)
    private String paymentMethod;

    @Column(name = "reference_no", length = 80)
    private String referenceNo;
}
