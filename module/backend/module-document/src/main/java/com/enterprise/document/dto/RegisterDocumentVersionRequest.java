package com.enterprise.document.dto;

import lombok.Data;

/**
 * @file RegisterDocumentVersionRequest.java
 * @description 登錄文件新版中繼資料請求 / Register document version metadata request
 */
@Data
public class RegisterDocumentVersionRequest {
    private String filePath;
    private String mimeType;
    private Long size;
    private String uploadedBy;
}
