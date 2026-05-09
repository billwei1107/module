package com.enterprise.document.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @file DocumentModuleConfig.java
 * @description 文件管理模組配置 / Document module configuration
 * @description_en Enables document components, entities, and repositories when the module is active
 * @description_zh 模組啟用時載入文件管理相關元件、實體與資料存取層
 */
@Configuration
@ConditionalOnProperty(prefix = "modules", name = "document", havingValue = "true", matchIfMissing = true)
@ComponentScan(basePackages = "com.enterprise.document")
@EntityScan(basePackages = "com.enterprise.document.entity")
@EnableJpaRepositories(basePackages = "com.enterprise.document.repository")
public class DocumentModuleConfig {
}
