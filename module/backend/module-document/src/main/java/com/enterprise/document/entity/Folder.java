package com.enterprise.document.entity;

import com.enterprise.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

/**
 * @file Folder.java
 * @description 文件資料夾實體 / Document folder entity
 * @description_en Stores hierarchical folder metadata
 * @description_zh 儲存樹狀資料夾中繼資料
 */
@Entity
@Table(name = "doc_folders")
@Data
@EqualsAndHashCode(callSuper = true)
public class Folder extends BaseEntity {

    @Column(name = "parent_id")
    private UUID parentId;

    @Column(nullable = false, length = 160)
    private String name;

    @Column(nullable = false, length = 500)
    private String path = "/";

    @Column(name = "owner_id", length = 80)
    private String ownerId;
}
