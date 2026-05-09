package com.enterprise.document.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @file FolderDTO.java
 * @description 資料夾回傳資料 / Folder response DTO
 */
@Data
@Builder
public class FolderDTO {
    private String id;
    private String parentId;
    private String name;
    private String path;
    private String ownerId;
}
