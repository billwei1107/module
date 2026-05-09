package com.enterprise.system.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @file DictionaryItemDTO.java
 * @description 資料字典項目回傳 / Dictionary item response DTO
 */
@Data
@Builder
public class DictionaryItemDTO {
    private String id;
    private String dictionaryId;
    private String label;
    private String value;
    private Integer sortOrder;
    private Boolean active;
}
