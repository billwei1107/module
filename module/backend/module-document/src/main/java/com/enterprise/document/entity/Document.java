package com.enterprise.document.entity;

import com.enterprise.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

/**
 * @file Document.java
 * @description 文件實體 / Document entity
 * @description_en Stores active document metadata and current version pointer
 * @description_zh 儲存文件中繼資料與目前版本資訊
 */
@Entity
@Table(name = "doc_documents")
@Data
@EqualsAndHashCode(callSuper = true)
public class Document extends BaseEntity {

    @Column(name = "folder_id")
    private UUID folderId;

    @Column(name = "file_name", nullable = false, length = 220)
    private String fileName;

    @Column(name = "file_path", nullable = false, length = 800)
    private String filePath;

    @Column(name = "mime_type", nullable = false, length = 120)
    private String mimeType;

    @Column(name = "file_size", nullable = false)
    private Long size = 0L;

    @Column(nullable = false)
    private Integer version = 1;

    @Column(name = "owner_id", length = 80)
    private String ownerId;
}
