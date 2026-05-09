package com.enterprise.document.entity;

import com.enterprise.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

/**
 * @file DocumentTag.java
 * @description 文件標籤實體 / Document tag entity
 * @description_en Stores lightweight tags assigned to documents
 * @description_zh 儲存文件使用的輕量分類標籤
 */
@Entity
@Table(name = "doc_tags")
@Data
@EqualsAndHashCode(callSuper = true)
public class DocumentTag extends BaseEntity {

    @Column(name = "document_id", nullable = false)
    private UUID documentId;

    @Column(nullable = false, length = 80)
    private String name;

    @Column(nullable = false, length = 24)
    private String color = "#64748b";
}
