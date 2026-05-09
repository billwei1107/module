package com.enterprise.document.dto;

import lombok.Data;

/**
 * @file RegisterDocumentRequest.java
 * @description 登錄文件中繼資料請求 / Register document metadata request
 */
@Data
public class RegisterDocumentRequest {
    private String folderId;
    private String fileName;
    private String filePath;
    private String mimeType;
    private Long size;
    private String ownerId;
}
