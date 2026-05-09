package com.enterprise.inventory.entity;

import com.enterprise.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * @file StockTake.java
 * @description 盤點實體 / Stock take entity
 * @description_en Stores frozen expected quantity, counted quantity, and difference
 * @description_zh 儲存盤點凍結帳面量、實盤量與差異
 */
@Entity
@Table(name = "inv_stock_takes")
@Data
@EqualsAndHashCode(callSuper = true)
public class StockTake extends BaseEntity {
    public enum StockTakeStatus { FROZEN, COUNTED, ADJUSTED }

    @Column(name = "item_id", nullable = false)
    private UUID itemId;
    @Column(name = "warehouse_id", nullable = false)
    private UUID warehouseId;
    @Column(name = "expected_quantity", nullable = false, precision = 19, scale = 4)
    private BigDecimal expectedQuantity = BigDecimal.ZERO;
    @Column(name = "actual_quantity", precision = 19, scale = 4)
    private BigDecimal actualQuantity;
    @Column(name = "difference_quantity", precision = 19, scale = 4)
    private BigDecimal differenceQuantity;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StockTakeStatus status = StockTakeStatus.FROZEN;
}
