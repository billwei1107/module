package com.enterprise.crm.entity;

import com.enterprise.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * @file QuotationItem.java
 * @description 報價單項目實體 / Quotation item entity
 * @description_en Stores line item quantity, unit price, and amount
 * @description_zh 儲存報價單項目的數量、單價與金額
 */
@Entity
@Table(name = "crm_quotation_items")
@Data
@EqualsAndHashCode(callSuper = true)
public class QuotationItem extends BaseEntity {
    @Column(name = "quotation_id", nullable = false)
    private UUID quotationId;
    @Column(nullable = false, length = 180)
    private String itemName;
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal quantity = BigDecimal.ZERO;
    @Column(name = "unit_price", nullable = false, precision = 19, scale = 4)
    private BigDecimal unitPrice = BigDecimal.ZERO;
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount = BigDecimal.ZERO;
}
