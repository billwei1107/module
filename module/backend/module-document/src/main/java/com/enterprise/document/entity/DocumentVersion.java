package com.enterprise.document.entity;

import com.enterprise.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

/**
 * @file DocumentVersion.java
 * @description 文件版本實體 / Document version entity
 * @description_en Stores immutable metadata for each uploaded document version
 * @description_zh 儲存每個文件版本不可變中繼資料
 */
@Entity
@Table(name = "doc_versions")
@Data
@EqualsAndHashCode(callSuper = true)
public class DocumentVersion extends BaseEntity {

    @Column(name = "document_id", nullable = false)
    private UUID documentId;

    @Column(nullable = false)
    private Integer version;

    @Column(name = "file_path", nullable = false, length = 800)
    private String filePath;

    @Column(name = "mime_type", nullable = false, length = 120)
    private String mimeType;

    @Column(name = "file_size", nullable = false)
    private Long size = 0L;

    @Column(name = "uploaded_by", length = 80)
    private String uploadedBy;
}
