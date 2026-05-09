package com.enterprise.project.entity;

import com.enterprise.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

/**
 * @file TaskAttachment.java
 * @description 任務附件實體 / Task attachment entity
 * @description_en Stores task attachment metadata without coupling to storage implementation
 * @description_zh 儲存任務附件中繼資料，不綁定具體儲存實作
 */
@Entity
@Table(name = "proj_task_attachments")
@Data
@EqualsAndHashCode(callSuper = true)
public class TaskAttachment extends BaseEntity {

    @Column(name = "task_id", nullable = false)
    private UUID taskId;

    @Column(name = "file_name", nullable = false, length = 180)
    private String fileName;

    @Column(name = "file_url", nullable = false, length = 500)
    private String fileUrl;

    @Column(name = "uploaded_by", length = 80)
    private String uploadedBy;
}
