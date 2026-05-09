package com.enterprise.project.entity;

import com.enterprise.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * @file Project.java
 * @description 專案實體 / Project entity
 * @description_en Stores project master data and schedule range
 * @description_zh 儲存專案主檔與起迄時程
 */
@Entity
@Table(name = "proj_projects")
@Data
@EqualsAndHashCode(callSuper = true)
public class Project extends BaseEntity {

    public enum ProjectStatus {
        PLANNING, ACTIVE, COMPLETED, ARCHIVED
    }

    @Column(nullable = false, length = 160)
    private String name;

    @Column(name = "owner_id", length = 80)
    private String ownerId;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProjectStatus status = ProjectStatus.PLANNING;

    @Column(columnDefinition = "TEXT")
    private String description;
}
