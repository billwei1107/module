package com.enterprise.system.dto;

import lombok.Data;

/**
 * @file UpdateSystemConfigRequest.java
 * @description 更新系統設定請求 / Update system config request
 */
@Data
public class UpdateSystemConfigRequest {
    private String key;
    private String value;
    private String category;
    private String description;
}
