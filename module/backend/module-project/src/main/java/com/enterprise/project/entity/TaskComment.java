package com.enterprise.project.entity;

import com.enterprise.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

/**
 * @file TaskComment.java
 * @description 任務留言實體 / Task comment entity
 * @description_en Stores task discussion comments
 * @description_zh 儲存任務討論留言
 */
@Entity
@Table(name = "proj_task_comments")
@Data
@EqualsAndHashCode(callSuper = true)
public class TaskComment extends BaseEntity {

    @Column(name = "task_id", nullable = false)
    private UUID taskId;

    @Column(name = "author_id", nullable = false, length = 80)
    private String authorId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
}
