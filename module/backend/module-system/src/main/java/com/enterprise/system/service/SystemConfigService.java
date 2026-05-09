package com.enterprise.system.service;

import com.enterprise.system.dto.SystemConfigDTO;
import com.enterprise.system.dto.UpdateSystemConfigRequest;

import java.util.List;

/**
 * @file SystemConfigService.java
 * @description 系統設定服務介面 / System config service interface
 */
public interface SystemConfigService {
    List<SystemConfigDTO> getConfigs(String category);

    SystemConfigDTO upsertConfig(UpdateSystemConfigRequest request);
}
