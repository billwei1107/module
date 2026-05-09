package com.enterprise.finance.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @file FinanceModuleConfig.java
 * @description 財務管理模組配置 / Finance module configuration
 * @description_en Enables finance components, entities, and repositories when the module is active
 * @description_zh 模組啟用時載入財務管理相關元件、實體與資料存取層
 */
@Configuration
@ConditionalOnProperty(prefix = "modules", name = "finance", havingValue = "true", matchIfMissing = true)
@ComponentScan(basePackages = "com.enterprise.finance")
@EntityScan(basePackages = "com.enterprise.finance.entity")
@EnableJpaRepositories(basePackages = "com.enterprise.finance.repository")
public class FinanceModuleConfig {
}
