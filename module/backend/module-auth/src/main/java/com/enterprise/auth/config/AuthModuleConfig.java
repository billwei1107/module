package com.enterprise.auth.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * @file AuthModuleConfig.java
 * @description 認證模組開關配置 / Authentication module configuration
 */
@Configuration
@ComponentScan(basePackages = "com.enterprise.auth")
@ConditionalOnProperty(prefix = "modules", name = "auth", havingValue = "true", matchIfMissing = true)
public class AuthModuleConfig {
    // 當 modules.auth 為 true 或未指定時，此配置類及其 ComponentScan 才會生效
}
