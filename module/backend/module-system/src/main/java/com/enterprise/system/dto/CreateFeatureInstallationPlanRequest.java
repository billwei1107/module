package com.enterprise.system.dto;

import lombok.Data;

import java.util.List;

/**
 * @file CreateFeatureInstallationPlanRequest.java
 * @description 模組安裝計畫請求 / Feature installation plan request
 * @description_en Carries selected module keys for dependency expansion
 * @description_zh 承載欲導入模組代碼，用於展開依賴與搬移路徑清單
 */
@Data
public class CreateFeatureInstallationPlanRequest {
    private List<String> selectedModules;
}
