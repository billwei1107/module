package com.enterprise.system.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @file FeatureInstallationPlanDTO.java
 * @description 模組安裝計畫回傳 / Feature installation plan response
 * @description_en Lists transitive dependencies and reusable source paths for selected modules
 * @description_zh 列出所選模組的遞迴依賴、額外必備模組與可搬移源碼路徑
 */
@Data
@Builder
public class FeatureInstallationPlanDTO {
    private List<String> requestedModules;
    private List<String> requiredModules;
    private List<String> additionalModules;
    private List<String> unknownModules;
    private List<String> backendModules;
    private List<String> frontendFeatures;
    private List<String> flywayLocations;
    private List<String> defaultPaths;
    private List<FeatureToggleDTO> modules;
}
