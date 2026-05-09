package com.enterprise.document.dto;

import lombok.Data;

/**
 * @file CreateFolderRequest.java
 * @description 建立資料夾請求 / Create folder request
 */
@Data
public class CreateFolderRequest {
    private String parentId;
    private String name;
    private String ownerId;
}
