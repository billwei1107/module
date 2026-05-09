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
 * @file Item.java
 * @description 品項實體 / Inventory item entity
 * @description_en Stores SKU, barcode, specification, and safety stock threshold
 * @description_zh 儲存品項 SKU、條碼、規格與安全庫存門檻
 */
@Entity
@Table(name = "inv_items")
@Data
@EqualsAndHashCode(callSuper = true)
public class Item extends BaseEntity {
    @Column(name = "category_id")
    private UUID categoryId;
    @Column(nullable = false, unique = true, length = 80)
    private String sku;
    @Column(nullable = false, length = 180)
    private String name;
    @Column(length = 120)
    private String specification;
    @Column(length = 80)
    private String barcode;
    @Column(nullable = false, length = 20)
    private String unit = "PCS";
    @Column(name = "safety_stock", nullable = false, precision = 19, scale = 4)
    private BigDecimal safetyStock = BigDecimal.ZERO;
}
