package com.enterprise.organization.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 * @file OrganizationModuleConfig.java
 * @description Organization Module 設定 / Organization Module Configuration
 * @description_en Configuration entry for the organization module, loaded based on property.
 * @description_zh 組織架構模組的配置入口，根據 application 屬性動態載入。
 */
@Slf4j
@Configuration
@ComponentScan(basePackages = "com.enterprise.organization")
@ConditionalOnProperty(prefix = "modules", name = "organization", havingValue = "true", matchIfMissing = true)

public class OrganizationModuleConfig {

    @PostConstruct
    public void init() {
        log.info("[Module-Organization] 組織架構模組載入成功...");
    }
}
