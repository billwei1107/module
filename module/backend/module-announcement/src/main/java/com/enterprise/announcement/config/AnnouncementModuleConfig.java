package com.enterprise.announcement.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @file AnnouncementModuleConfig.java
 * @description 公告系統模組配置 / Announcement module configuration
 * @description_en Enables announcement components, entities, and repositories when active
 * @description_zh 模組啟用時載入公告系統相關元件、實體與資料存取層
 */
@Configuration
@ConditionalOnProperty(prefix = "modules", name = "announcement", havingValue = "true", matchIfMissing = true)
@ComponentScan(basePackages = "com.enterprise.announcement")
@EntityScan(basePackages = "com.enterprise.announcement.entity")
@EnableJpaRepositories(basePackages = "com.enterprise.announcement.repository")
public class AnnouncementModuleConfig {
}
