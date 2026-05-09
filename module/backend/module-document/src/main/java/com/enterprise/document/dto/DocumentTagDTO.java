package com.enterprise.document.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @file DocumentTagDTO.java
 * @description 文件標籤回傳資料 / Document tag response DTO
 */
@Data
@Builder
public class DocumentTagDTO {
    private String id;
    private String documentId;
    private String name;
    private String color;
}
