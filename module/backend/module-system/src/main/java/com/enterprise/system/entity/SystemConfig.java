package com.enterprise.system.entity;

import com.enterprise.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @file SystemConfig.java
 * @description 系統設定實體 / System config entity
 * @description_zh 儲存系統名稱、公司資訊與其他全域設定
 */
@Entity
@Table(name = "sys_configs")
@Data
@EqualsAndHashCode(callSuper = true)
public class SystemConfig extends BaseEntity {

    @Column(name = "config_key", nullable = false, unique = true, length = 100)
    private String key;

    @Column(name = "config_value", columnDefinition = "TEXT")
    private String value;

    @Column(nullable = false, length = 50)
    private String category = "general";

    @Column(columnDefinition = "TEXT")
    private String description;
}
