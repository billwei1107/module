package com.enterprise.crm.entity;

import com.enterprise.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * @file Quotation.java
 * @description 報價單實體 / Quotation entity
 * @description_en Stores quotation totals with tax-inclusive or tax-exclusive calculation
 * @description_zh 儲存報價單含稅或未稅計算後的總額
 */
@Entity
@Table(name = "crm_quotations")
@Data
@EqualsAndHashCode(callSuper = true)
public class Quotation extends BaseEntity {
    public enum QuotationStatus { DRAFT, SENT, ACCEPTED, REJECTED }

    @Column(name = "quotation_no", nullable = false, unique = true, length = 60)
    private String quotationNo;
    @Column(name = "customer_id", nullable = false)
    private UUID customerId;
    @Column(name = "opportunity_id")
    private UUID opportunityId;
    @Column(name = "quote_date", nullable = false)
    private LocalDate quoteDate;
    @Column(name = "tax_inclusive", nullable = false)
    private Boolean taxInclusive = false;
    @Column(name = "tax_rate", nullable = false, precision = 8, scale = 4)
    private BigDecimal taxRate = BigDecimal.ZERO;
    @Column(name = "subtotal", nullable = false, precision = 19, scale = 4)
    private BigDecimal subtotal = BigDecimal.ZERO;
    @Column(name = "tax_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal taxAmount = BigDecimal.ZERO;
    @Column(name = "total_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal totalAmount = BigDecimal.ZERO;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private QuotationStatus status = QuotationStatus.DRAFT;
}
