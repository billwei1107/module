package com.enterprise.report.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @file ReportModuleConfig.java
 * @description 報表分析模組配置 / Report module configuration
 * @description_en Enables report components, entities, and repositories when the module is active
 * @description_zh 模組啟用時載入報表分析相關元件、實體與資料存取層
 */
@Configuration
@ConditionalOnProperty(prefix = "modules", name = "report", havingValue = "true", matchIfMissing = true)
@ComponentScan(basePackages = "com.enterprise.report")
@EntityScan(basePackages = "com.enterprise.report.entity")
@EnableJpaRepositories(basePackages = "com.enterprise.report.repository")
public class ReportModuleConfig {
}
