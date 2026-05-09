package com.enterprise.system.service.impl;

import com.enterprise.system.dto.FeatureToggleDTO;
import com.enterprise.system.service.FeatureToggleService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @file FeatureToggleServiceImpl.java
 * @description 功能開關服務實作 / Feature toggle service implementation
 * @description_zh 從 modules.* 設定讀取各模組啟用狀態
 */
@Service
@RequiredArgsConstructor
public class FeatureToggleServiceImpl implements FeatureToggleService {

    private static final List<String> MODULE_NAMES = List.of(
            "auth", "organization", "workflow", "notification", "attendance",
            "leave", "system", "audit", "finance", "payroll", "project",
            "document", "report", "crm", "inventory", "meeting", "announcement"
    );

    private final Environment environment;

    @Override
    public List<FeatureToggleDTO> getFeatures() {
        return MODULE_NAMES.stream()
                .map(module -> FeatureToggleDTO.builder()
                        .module(module)
                        .enabled(environment.getProperty("modules." + module, Boolean.class, false))
                        .build())
                .toList();
    }
}
