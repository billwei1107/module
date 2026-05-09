package com.enterprise.inventory.entity;

import com.enterprise.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * @file StockRecord.java
 * @description 庫存紀錄實體 / Stock record entity
 * @description_en Stores current quantity for an item in a warehouse
 * @description_zh 儲存指定倉庫內品項目前庫存數量
 */
@Entity
@Table(name = "inv_records")
@Data
@EqualsAndHashCode(callSuper = true)
public class StockRecord extends BaseEntity {
    @Column(name = "item_id", nullable = false)
    private UUID itemId;
    @Column(name = "warehouse_id", nullable = false)
    private UUID warehouseId;
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal quantity = BigDecimal.ZERO;
}
