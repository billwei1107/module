package com.enterprise.system.dto;

import lombok.Data;

/**
 * @file CreateDictionaryRequest.java
 * @description 建立資料字典請求 / Create dictionary request
 */
@Data
public class CreateDictionaryRequest {
    private String code;
    private String name;
}
