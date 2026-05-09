package com.enterprise.document.entity;

import com.enterprise.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @file DocumentShare.java
 * @description 文件分享實體 / Document share entity
 * @description_en Stores user-level document permissions and optional expiration
 * @description_zh 儲存文件對指定使用者的權限與可選到期時間
 */
@Entity
@Table(name = "doc_shares")
@Data
@EqualsAndHashCode(callSuper = true)
public class DocumentShare extends BaseEntity {

    public enum Permission {
        READ, EDIT, SHARE
    }

    @Column(name = "document_id", nullable = false)
    private UUID documentId;

    @Column(name = "shared_with", nullable = false, length = 80)
    private String sharedWith;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Permission permission = Permission.READ;

    @Column(name = "shared_by", length = 80)
    private String sharedBy;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
}
