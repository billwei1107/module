package com.enterprise.inventory.entity;

import com.enterprise.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @file Category.java
 * @description 品項分類實體 / Inventory category entity
 * @description_en Stores item category metadata
 * @description_zh 儲存品項分類中繼資料
 */
@Entity
@Table(name = "inv_categories")
@Data
@EqualsAndHashCode(callSuper = true)
public class Category extends BaseEntity {
    @Column(nullable = false, length = 120)
    private String name;
    @Column(length = 40)
    private String code;
}
