package com.enterprise.inventory.entity;

import com.enterprise.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @file Warehouse.java
 * @description 倉庫實體 / Warehouse entity
 * @description_en Stores warehouse master data
 * @description_zh 儲存倉庫主檔資料
 */
@Entity
@Table(name = "inv_warehouses")
@Data
@EqualsAndHashCode(callSuper = true)
public class Warehouse extends BaseEntity {
    @Column(nullable = false, unique = true, length = 60)
    private String code;
    @Column(nullable = false, length = 160)
    private String name;
    @Column(length = 240)
    private String location;
}
