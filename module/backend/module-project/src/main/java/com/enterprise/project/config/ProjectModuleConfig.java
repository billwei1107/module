package com.enterprise.project.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @file ProjectModuleConfig.java
 * @description 專案任務模組配置 / Project module configuration
 * @description_en Enables project components, entities, and repositories when the module is active
 * @description_zh 模組啟用時載入專案任務相關元件、實體與資料存取層
 */
@Configuration
@ConditionalOnProperty(prefix = "modules", name = "project", havingValue = "true", matchIfMissing = true)
@ComponentScan(basePackages = "com.enterprise.project")
@EntityScan(basePackages = "com.enterprise.project.entity")
@EnableJpaRepositories(basePackages = "com.enterprise.project.repository")
public class ProjectModuleConfig {
}
