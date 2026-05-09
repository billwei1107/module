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
import java.util.UUID;

/**
 * @file Task.java
 * @description 任務實體 / Task entity
 * @description_en Stores Kanban task data, parent task, dependencies, and due date
 * @description_zh 儲存看板任務、子任務、前置依賴與到期日
 */
@Entity
@Table(name = "proj_tasks")
@Data
@EqualsAndHashCode(callSuper = true)
public class Task extends BaseEntity {

    public enum TaskStatus {
        TODO, IN_PROGRESS, DONE
    }

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(nullable = false, length = 160)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "assignee_id", length = 80)
    private String assigneeId;

    @Column(name = "parent_id")
    private UUID parentId;

    @Column(name = "dependency_ids", columnDefinition = "TEXT")
    private String dependencyIds;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TaskStatus status = TaskStatus.TODO;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "due_date")
    private LocalDate dueDate;
}
