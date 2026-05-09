package com.enterprise.system.service;

import com.enterprise.system.dto.FeatureDependencyIssueDTO;
import com.enterprise.system.dto.FeatureInstallationPlanDTO;
import com.enterprise.system.dto.FeatureToggleDTO;

import java.util.List;

/**
 * @file FeatureToggleService.java
 * @description 功能開關與依賴檢查服務介面 / Feature toggle and dependency validation service interface
 */
public interface FeatureToggleService {
    List<FeatureToggleDTO> getFeatures();

    List<FeatureDependencyIssueDTO> getDependencyIssues();

    FeatureInstallationPlanDTO createInstallationPlan(List<String> selectedModules);

    FeatureInstallationPlanDTO getCurrentInstallationPlan();
}
