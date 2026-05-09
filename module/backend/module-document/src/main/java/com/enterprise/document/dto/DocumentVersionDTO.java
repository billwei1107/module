package com.enterprise.document.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @file DocumentVersionDTO.java
 * @description 文件版本回傳資料 / Document version response DTO
 */
@Data
@Builder
public class DocumentVersionDTO {
    private String id;
    private String documentId;
    private Integer version;
    private String filePath;
    private String mimeType;
    private Long size;
    private String uploadedBy;
}
