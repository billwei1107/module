package com.enterprise.document.dto;

import com.enterprise.document.entity.DocumentShare.Permission;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @file ShareDocumentRequest.java
 * @description 分享文件請求 / Share document request
 */
@Data
public class ShareDocumentRequest {
    private String sharedWith;
    private Permission permission = Permission.READ;
    private String sharedBy;
    private LocalDateTime expiresAt;
}
