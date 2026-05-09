package com.enterprise.report.entity;

import com.enterprise.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @file DashboardConfig.java
 * @description 儀表板配置實體 / Dashboard configuration entity
 * @description_en Stores dashboard layout metadata for reusable report widgets
 * @description_zh 儲存可重用報表元件的儀表板版面中繼資料
 */
@Entity
@Table(name = "rpt_dashboard_configs")
@Data
@EqualsAndHashCode(callSuper = true)
public class DashboardConfig extends BaseEntity {

    @Column(nullable = false, length = 160)
    private String name;

    @Column(name = "owner_id", length = 80)
    private String ownerId;

    @Column(name = "layout_json", nullable = false, columnDefinition = "TEXT")
    private String layoutJson = "{}";

    @Column(nullable = false)
    private Boolean active = true;
}
