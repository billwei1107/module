package com.enterprise.crm.entity;

import com.enterprise.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * @file Opportunity.java
 * @description 商機實體 / Opportunity entity
 * @description_en Tracks sales pipeline stages and expected revenue
 * @description_zh 追蹤銷售漏斗階段與預期金額
 */
@Entity
@Table(name = "crm_opportunities")
@Data
@EqualsAndHashCode(callSuper = true)
public class Opportunity extends BaseEntity {
    public enum Stage { LEAD, QUALIFIED, PROPOSAL, NEGOTIATION, CLOSED_WON, CLOSED_LOST }

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;
    @Column(nullable = false, length = 180)
    private String name;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Stage stage = Stage.LEAD;
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount = BigDecimal.ZERO;
    @Column(name = "expected_close_date")
    private LocalDate expectedCloseDate;
    @Column(name = "owner_id", length = 80)
    private String ownerId;
}
