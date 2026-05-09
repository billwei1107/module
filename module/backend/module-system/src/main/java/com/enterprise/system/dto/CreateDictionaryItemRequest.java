package com.enterprise.system.dto;

import lombok.Data;

/**
 * @file CreateDictionaryItemRequest.java
 * @description 建立資料字典項目請求 / Create dictionary item request
 */
@Data
public class CreateDictionaryItemRequest {
    private String label;
    private String value;
    private Integer sortOrder;
}
