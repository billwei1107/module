package com.enterprise.report.entity;

import com.enterprise.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @file ReportDefinition.java
 * @description 報表定義實體 / Report definition entity
 * @description_en Stores configurable report SQL and visible column metadata
 * @description_zh 儲存可配置報表 SQL 與顯示欄位中繼資料
 */
@Entity
@Table(name = "rpt_definitions")
@Data
@EqualsAndHashCode(callSuper = true)
public class ReportDefinition extends BaseEntity {

    @Column(nullable = false, length = 160)
    private String name;

    @Column(name = "data_source_sql", nullable = false, columnDefinition = "TEXT")
    private String dataSourceSql;

    @Column(name = "columns_json", nullable = false, columnDefinition = "TEXT")
    private String columnsJson = "[]";

    @Column(name = "filters_json", nullable = false, columnDefinition = "TEXT")
    private String filtersJson = "{}";

    @Column(nullable = false)
    private Boolean active = true;
}
