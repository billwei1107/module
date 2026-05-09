package com.enterprise.system.entity;

import com.enterprise.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

/**
 * @file DictionaryItem.java
 * @description 資料字典項目實體 / Dictionary item entity
 * @description_zh 儲存資料字典下的 label/value 選項
 */
@Entity
@Table(name = "sys_dictionary_items")
@Data
@EqualsAndHashCode(callSuper = true)
public class DictionaryItem extends BaseEntity {

    @Column(name = "dictionary_id", nullable = false)
    private UUID dictionaryId;

    @Column(nullable = false, length = 100)
    private String label;

    @Column(nullable = false, length = 100)
    private String value;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    @Column(nullable = false)
    private Boolean active = true;
}
