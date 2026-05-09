package com.enterprise.system.service.impl;

import com.enterprise.common.annotation.Auditable;
import com.enterprise.common.exception.BusinessException;
import com.enterprise.system.dto.SystemConfigDTO;
import com.enterprise.system.dto.UpdateSystemConfigRequest;
import com.enterprise.system.entity.SystemConfig;
import com.enterprise.system.repository.SystemConfigRepository;
import com.enterprise.system.service.SystemConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @file SystemConfigServiceImpl.java
 * @description 系統設定服務實作 / System config service implementation
 * @description_zh 提供全域設定查詢與 upsert，未來可接 Redis 快取
 */
@Service
@RequiredArgsConstructor
public class SystemConfigServiceImpl implements SystemConfigService {

    private final SystemConfigRepository configRepository;

    @Override
    public List<SystemConfigDTO> getConfigs(String category) {
        List<SystemConfig> configs = category == null
                ? configRepository.findByDeletedAtIsNullOrderByCategoryAscKeyAsc()
                : configRepository.findByCategoryAndDeletedAtIsNullOrderByKeyAsc(category);
        return configs.stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional
    @Auditable(module = "system", action = "UPSERT_SYSTEM_CONFIG")
    public SystemConfigDTO upsertConfig(UpdateSystemConfigRequest request) {
        if (request.getKey() == null || request.getKey().isBlank()) {
            throw new BusinessException(400, "設定鍵不可為空 / Config key is required");
        }

        SystemConfig config = configRepository.findByKeyAndDeletedAtIsNull(request.getKey())
                .orElseGet(SystemConfig::new);
        config.setKey(request.getKey());
        config.setValue(request.getValue());
        config.setCategory(request.getCategory() == null ? "general" : request.getCategory());
        config.setDescription(request.getDescription());
        return toDTO(configRepository.save(config));
    }

    private SystemConfigDTO toDTO(SystemConfig config) {
        return SystemConfigDTO.builder()
                .id(config.getId() != null ? config.getId().toString() : null)
                .key(config.getKey())
                .value(config.getValue())
                .category(config.getCategory())
                .description(config.getDescription())
                .build();
    }
}
