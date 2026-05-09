package com.enterprise.system.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @file SystemConfigDTO.java
 * @description 系統設定回傳 / System config response DTO
 */
@Data
@Builder
public class SystemConfigDTO {
    private String id;
    private String key;
    private String value;
    private String category;
    private String description;
}
