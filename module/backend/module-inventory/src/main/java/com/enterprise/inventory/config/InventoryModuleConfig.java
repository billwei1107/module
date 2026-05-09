package com.enterprise.inventory.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @file InventoryModuleConfig.java
 * @description 庫存管理模組配置 / Inventory module configuration
 * @description_en Enables inventory components, entities, and repositories when the module is active
 * @description_zh 模組啟用時載入庫存管理相關元件、實體與資料存取層
 */
@Configuration
@ConditionalOnProperty(prefix = "modules", name = "inventory", havingValue = "true", matchIfMissing = true)
@ComponentScan(basePackages = "com.enterprise.inventory")
@EntityScan(basePackages = "com.enterprise.inventory.entity")
@EnableJpaRepositories(basePackages = "com.enterprise.inventory.repository")
public class InventoryModuleConfig {
}
