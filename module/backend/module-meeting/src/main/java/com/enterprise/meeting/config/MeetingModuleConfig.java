package com.enterprise.meeting.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @file MeetingModuleConfig.java
 * @description 會議管理模組配置 / Meeting module configuration
 * @description_en Enables meeting components, entities, and repositories when active
 * @description_zh 模組啟用時載入會議管理相關元件、實體與資料存取層
 */
@Configuration
@ConditionalOnProperty(prefix = "modules", name = "meeting", havingValue = "true", matchIfMissing = true)
@ComponentScan(basePackages = "com.enterprise.meeting")
@EntityScan(basePackages = "com.enterprise.meeting.entity")
@EnableJpaRepositories(basePackages = "com.enterprise.meeting.repository")
public class MeetingModuleConfig {
}
