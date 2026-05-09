package com.enterprise.inventory.entity;

import com.enterprise.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * @file StockMovement.java
 * @description 庫存異動實體 / Stock movement entity
 * @description_en Stores inbound, outbound, transfer, and adjustment movement logs
 * @description_zh 儲存入庫、出庫、調撥與調整異動紀錄
 */
@Entity
@Table(name = "inv_movements")
@Data
@EqualsAndHashCode(callSuper = true)
public class StockMovement extends BaseEntity {
    public enum MovementType { INBOUND, OUTBOUND, TRANSFER, ADJUSTMENT }

    @Column(name = "item_id", nullable = false)
    private UUID itemId;
    @Column(name = "from_warehouse_id")
    private UUID fromWarehouseId;
    @Column(name = "to_warehouse_id")
    private UUID toWarehouseId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MovementType type;
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal quantity = BigDecimal.ZERO;
    @Column(name = "reference_no", length = 80)
    private String referenceNo;
    @Column(columnDefinition = "TEXT")
    private String note;
}
