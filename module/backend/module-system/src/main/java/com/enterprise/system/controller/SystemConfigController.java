package com.enterprise.system.controller;

import com.enterprise.common.dto.ApiResponse;
import com.enterprise.system.dto.SystemConfigDTO;
import com.enterprise.system.dto.UpdateSystemConfigRequest;
import com.enterprise.system.service.SystemConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @file SystemConfigController.java
 * @description 系統設定控制器 / System config controller
 */
@RestController
@RequestMapping("/api/v1/system/configs")
@RequiredArgsConstructor
public class SystemConfigController {

    private final SystemConfigService configService;

    @GetMapping
    public ApiResponse<List<SystemConfigDTO>> getConfigs(@RequestParam(required = false) String category) {
        return ApiResponse.success(configService.getConfigs(category));
    }

    @PutMapping
    public ApiResponse<SystemConfigDTO> upsertConfig(@RequestBody UpdateSystemConfigRequest request) {
        return ApiResponse.success(configService.upsertConfig(request));
    }
}
