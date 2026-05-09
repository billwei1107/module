package com.enterprise.system.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @file SystemModuleConfig.java
 * @description 系統設定模組配置 / System module configuration
 * @description_zh 解耦 JPA 與元件掃描路徑，確保系統模組可獨立啟停
 */
@Configuration
@ConditionalOnProperty(prefix = "modules", name = "system", havingValue = "true", matchIfMissing = true)
@ComponentScan(basePackages = "com.enterprise.system")
@EntityScan(basePackages = "com.enterprise.system.entity")
@EnableJpaRepositories(basePackages = "com.enterprise.system.repository")
public class SystemModuleConfig {
}
