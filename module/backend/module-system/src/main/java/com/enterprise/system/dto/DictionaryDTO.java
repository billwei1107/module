package com.enterprise.system.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @file DictionaryDTO.java
 * @description 資料字典回傳 / Dictionary response DTO
 */
@Data
@Builder
public class DictionaryDTO {
    private String id;
    private String code;
    private String name;
    private Boolean active;
    private List<DictionaryItemDTO> items;
}
