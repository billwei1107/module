package com.enterprise.audit.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @file AuditModuleConfig.java
 * @description 稽核日誌模組配置 / Audit module configuration
 * @description_en Enables audit components, entities, and repositories when the module is active
 * @description_zh 模組啟用時載入稽核日誌相關元件、實體與資料存取層
 */
@Configuration
@ConditionalOnProperty(prefix = "modules", name = "audit", havingValue = "true", matchIfMissing = true)
@ComponentScan(basePackages = "com.enterprise.audit")
@EntityScan(basePackages = "com.enterprise.audit.entity")
@EnableJpaRepositories(basePackages = "com.enterprise.audit.repository")
public class AuditModuleConfig {
}
