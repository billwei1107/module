package com.enterprise.system.service;

import com.enterprise.system.dto.FeatureToggleDTO;

import java.util.List;

/**
 * @file FeatureToggleService.java
 * @description 功能開關服務介面 / Feature toggle service interface
 */
public interface FeatureToggleService {
    List<FeatureToggleDTO> getFeatures();
}
