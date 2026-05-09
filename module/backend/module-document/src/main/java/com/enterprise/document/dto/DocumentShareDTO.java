package com.enterprise.document.dto;

import com.enterprise.document.entity.DocumentShare.Permission;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @file DocumentShareDTO.java
 * @description 文件分享回傳資料 / Document share response DTO
 */
@Data
@Builder
public class DocumentShareDTO {
    private String id;
    private String documentId;
    private String sharedWith;
    private Permission permission;
    private String sharedBy;
    private LocalDateTime expiresAt;
}
