package com.enterprise.project.entity;

import com.enterprise.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.UUID;

/**
 * @file Milestone.java
 * @description 里程碑實體 / Milestone entity
 * @description_en Stores important project dates and completion state
 * @description_zh 儲存專案重要節點日期與完成狀態
 */
@Entity
@Table(name = "proj_milestones")
@Data
@EqualsAndHashCode(callSuper = true)
public class Milestone extends BaseEntity {

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(nullable = false, length = 160)
    private String name;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(nullable = false)
    private Boolean completed = false;
}
