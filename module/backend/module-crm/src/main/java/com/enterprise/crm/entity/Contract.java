package com.enterprise.crm.entity;

import com.enterprise.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * @file Contract.java
 * @description 合約實體 / Contract entity
 * @description_en Tracks customer contracts and expiration status
 * @description_zh 追蹤客戶合約與到期狀態
 */
@Entity
@Table(name = "crm_contracts")
@Data
@EqualsAndHashCode(callSuper = true)
public class Contract extends BaseEntity {
    public enum ContractStatus { DRAFT, ACTIVE, EXPIRED, TERMINATED }

    @Column(name = "contract_no", nullable = false, unique = true, length = 60)
    private String contractNo;
    @Column(name = "customer_id", nullable = false)
    private UUID customerId;
    @Column(length = 180)
    private String title;
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount = BigDecimal.ZERO;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ContractStatus status = ContractStatus.DRAFT;
}
