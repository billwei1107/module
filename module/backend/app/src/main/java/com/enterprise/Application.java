package com.enterprise;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @file Application.java
 * @description 後端應用程式進入點 / Application Entry Point
 * @description_en Main Spring Boot application class
 * @description_zh 負責啟動整個 Spring Boot 後端微模塊系統
 */
@SpringBootApplication(scanBasePackages = {
        "com.enterprise.common",
        "com.enterprise.auth.config",
        "com.enterprise.organization.config",
        "com.enterprise.workflow.config",
        "com.enterprise.notification.config",
        "com.enterprise.attendance.config",
        "com.enterprise.leave.config",
        "com.enterprise.system.config",
        "com.enterprise.audit.config",
        "com.enterprise.finance.config",
        "com.enterprise.payroll.config",
        "com.enterprise.project.config",
        "com.enterprise.document.config",
        "com.enterprise.report.config",
        "com.enterprise.crm.config",
        "com.enterprise.inventory.config",
        "com.enterprise.meeting.config",
        "com.enterprise.announcement.config"
})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
