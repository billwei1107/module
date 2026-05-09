package com.enterprise.document.dto;

import com.enterprise.document.entity.DocumentShare.Permission;
import lombok.Builder;
import lombok.Data;

/**
 * @file DocumentAccessDTO.java
 * @description 文件權限檢查回傳 / Document access response DTO
 */
@Data
@Builder
public class DocumentAccessDTO {
    private String documentId;
    private String userId;
    private Permission requiredPermission;
    private Boolean allowed;
}
