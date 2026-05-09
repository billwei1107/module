package com.enterprise.leave.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @file LeaveModuleConfig.java
 * @description 請假管理模組配置 / Leave module configuration
 * @description_zh 解耦 JPA 與元件掃描路徑，確保請假模組可獨立啟停
 */
@Configuration
@ConditionalOnProperty(prefix = "modules", name = "leave", havingValue = "true", matchIfMissing = true)
@ComponentScan(basePackages = "com.enterprise.leave")
@EntityScan(basePackages = "com.enterprise.leave.entity")
@EnableJpaRepositories(basePackages = "com.enterprise.leave.repository")
public class LeaveModuleConfig {
}
