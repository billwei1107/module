package com.enterprise.system.entity;

import com.enterprise.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @file Dictionary.java
 * @description 資料字典實體 / Dictionary entity
 * @description_zh 定義通用下拉選項群組，例如員工狀態、假別分類等
 */
@Entity
@Table(name = "sys_dictionaries")
@Data
@EqualsAndHashCode(callSuper = true)
public class Dictionary extends BaseEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private Boolean active = true;
}
