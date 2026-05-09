package com.enterprise.system.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @file FeatureToggleDTO.java
 * @description 功能開關與模組清冊狀態回傳 / Feature toggle and module catalog status DTO
 * @description_en Describes module enabled state, dependency metadata, and reusable source locations
 * @description_zh 描述模組啟用狀態、依賴資訊與可複用源碼位置
 */
@Data
@Builder
public class FeatureToggleDTO {
    private String module;
    private Boolean enabled;
    private String displayName;
    private String displayNameEn;
    private String phase;
    private String priority;
    private String backendModule;
    private String frontendFeature;
    private String flywayLocation;
    private String defaultPath;
    private List<String> dependencies;
}
