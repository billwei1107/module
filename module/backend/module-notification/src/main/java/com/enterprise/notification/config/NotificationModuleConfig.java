package com.enterprise.notification.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ConditionalOnProperty(prefix = "modules", name = "notification", havingValue = "true", matchIfMissing = true)
@ComponentScan(basePackages = "com.enterprise.notification")
@EntityScan(basePackages = "com.enterprise.notification.entity")
@EnableJpaRepositories(basePackages = "com.enterprise.notification.repository")
public class NotificationModuleConfig {
}
