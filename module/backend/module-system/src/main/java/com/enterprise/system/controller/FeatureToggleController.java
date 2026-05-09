package com.enterprise.system.controller;

import com.enterprise.common.dto.ApiResponse;
import com.enterprise.system.dto.CreateFeatureInstallationPlanRequest;
import com.enterprise.system.dto.FeatureDependencyIssueDTO;
import com.enterprise.system.dto.FeatureInstallationPlanDTO;
import com.enterprise.system.dto.FeatureToggleDTO;
import com.enterprise.system.service.FeatureToggleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @file FeatureToggleController.java
 * @description 功能開關控制器 / Feature toggle controller
 */
@RestController
@RequestMapping("/api/v1/system/features")
@RequiredArgsConstructor
public class FeatureToggleController {

    private final FeatureToggleService featureToggleService;

    @GetMapping
    public ApiResponse<List<FeatureToggleDTO>> getFeatures() {
        return ApiResponse.success(featureToggleService.getFeatures());
    }

    @GetMapping("/dependency-issues")
    public ApiResponse<List<FeatureDependencyIssueDTO>> getDependencyIssues() {
        return ApiResponse.success(featureToggleService.getDependencyIssues());
    }

    @GetMapping("/installation-plan/current")
    public ApiResponse<FeatureInstallationPlanDTO> getCurrentInstallationPlan() {
        return ApiResponse.success(featureToggleService.getCurrentInstallationPlan());
    }

    @PostMapping("/installation-plan")
    public ApiResponse<FeatureInstallationPlanDTO> createInstallationPlan(@RequestBody(required = false) CreateFeatureInstallationPlanRequest request) {
        List<String> selectedModules = request == null ? List.of() : request.getSelectedModules();
        return ApiResponse.success(featureToggleService.createInstallationPlan(selectedModules));
    }
}
