package com.enterprise.system.controller;

import com.enterprise.common.dto.ApiResponse;
import com.enterprise.system.dto.FeatureToggleDTO;
import com.enterprise.system.service.FeatureToggleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
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
}
