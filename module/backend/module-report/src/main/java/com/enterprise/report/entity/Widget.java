package com.enterprise.report.entity;

import com.enterprise.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

/**
 * @file Widget.java
 * @description 儀表板圖表元件實體 / Dashboard widget entity
 * @description_en Stores chart type and data source SQL for dashboard cards
 * @description_zh 儲存儀表板卡片的圖表類型與資料來源 SQL
 */
@Entity
@Table(name = "rpt_widgets")
@Data
@EqualsAndHashCode(callSuper = true)
public class Widget extends BaseEntity {

    public enum WidgetType {
        BAR, LINE, PIE, NUMBER
    }

    @Column(name = "dashboard_id", nullable = false)
    private UUID dashboardId;

    @Column(nullable = false, length = 160)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private WidgetType type = WidgetType.NUMBER;

    @Column(name = "data_source_sql", nullable = false, columnDefinition = "TEXT")
    private String dataSourceSql;

    @Column(name = "position_json", nullable = false, columnDefinition = "TEXT")
    private String positionJson = "{}";
}
