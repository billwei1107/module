package com.enterprise.system.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @file FeatureToggleDTO.java
 * @description 功能開關狀態回傳 / Feature toggle status DTO
 */
@Data
@Builder
public class FeatureToggleDTO {
    private String module;
    private Boolean enabled;
}
