package com.enterprise.system.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @file FeatureDependencyIssueDTO.java
 * @description 功能開關依賴缺口回傳 / Feature toggle dependency issue DTO
 * @description_en Reports enabled modules whose required dependencies are disabled
 * @description_zh 回報已啟用但必要依賴未啟用的模組設定問題
 */
@Data
@Builder
public class FeatureDependencyIssueDTO {
    private String module;
    private String displayName;
    private List<String> missingDependencies;
    private String message;
}
