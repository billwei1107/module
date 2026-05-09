package com.enterprise.crm.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @file CrmModuleConfig.java
 * @description 客戶管理模組配置 / CRM module configuration
 * @description_en Enables CRM components, entities, and repositories when the module is active
 * @description_zh 模組啟用時載入客戶管理相關元件、實體與資料存取層
 */
@Configuration
@ConditionalOnProperty(prefix = "modules", name = "crm", havingValue = "true", matchIfMissing = true)
@ComponentScan(basePackages = "com.enterprise.crm")
@EntityScan(basePackages = "com.enterprise.crm.entity")
@EnableJpaRepositories(basePackages = "com.enterprise.crm.repository")
public class CrmModuleConfig {
}
