package com.enterprise.document.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @file DocumentDTO.java
 * @description 文件回傳資料 / Document response DTO
 */
@Data
@Builder
public class DocumentDTO {
    private String id;
    private String folderId;
    private String fileName;
    private String filePath;
    private String mimeType;
    private Long size;
    private Integer version;
    private String ownerId;
    private List<DocumentTagDTO> tags;
}
