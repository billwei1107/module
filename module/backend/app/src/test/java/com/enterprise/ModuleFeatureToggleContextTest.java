package com.enterprise;

import com.enterprise.announcement.controller.AnnouncementController;
import com.enterprise.announcement.service.impl.AnnouncementServiceImpl;
import com.enterprise.attendance.controller.AttendanceController;
import com.enterprise.attendance.service.impl.AttendanceServiceImpl;
import com.enterprise.audit.controller.AuditLogController;
import com.enterprise.audit.service.impl.AuditLogServiceImpl;
import com.enterprise.auth.controller.AuthController;
import com.enterprise.auth.service.impl.AuthServiceImpl;
import com.enterprise.crm.controller.CrmController;
import com.enterprise.crm.service.impl.CrmServiceImpl;
import com.enterprise.document.controller.DocumentController;
import com.enterprise.document.service.impl.DocumentServiceImpl;
import com.enterprise.finance.controller.AccountController;
import com.enterprise.finance.service.impl.AccountServiceImpl;
import com.enterprise.inventory.controller.InventoryController;
import com.enterprise.inventory.service.impl.InventoryServiceImpl;
import com.enterprise.leave.controller.LeaveController;
import com.enterprise.leave.service.impl.LeaveServiceImpl;
import com.enterprise.meeting.controller.MeetingController;
import com.enterprise.meeting.service.impl.MeetingServiceImpl;
import com.enterprise.notification.controller.NotificationController;
import com.enterprise.notification.service.impl.NotificationServiceImpl;
import com.enterprise.organization.controller.CompanyController;
import com.enterprise.organization.service.impl.CompanyServiceImpl;
import com.enterprise.payroll.controller.PayrollRecordController;
import com.enterprise.payroll.service.impl.PayrollCalculationServiceImpl;
import com.enterprise.project.controller.ProjectController;
import com.enterprise.project.service.impl.ProjectServiceImpl;
import com.enterprise.report.controller.ReportController;
import com.enterprise.report.service.impl.ReportServiceImpl;
import com.enterprise.system.controller.SystemConfigController;
import com.enterprise.system.service.impl.SystemConfigServiceImpl;
import com.enterprise.workflow.controller.WorkflowController;
import com.enterprise.workflow.engine.WorkflowEngine;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @file ModuleFeatureToggleContextTest.java
 * @description 模組啟停 Spring Context 測試 / Module feature toggle Spring context tests
 * @description_en Starts lightweight app contexts and verifies disabled modules do not register beans
 * @description_zh 啟動輕量 app context，驗證關閉的模組不會註冊業務 Bean
 */
class ModuleFeatureToggleContextTest {
    // ========================================
    // 模組清單 / Module List
    // ========================================
    private static final List<String> ALL_MODULES = List.of(
            "auth", "organization", "workflow", "notification", "attendance", "leave", "system", "audit",
            "finance", "payroll", "project", "document", "report", "crm", "inventory", "meeting", "announcement");

    // ========================================
    // 代表性模組 Bean / Representative Module Beans
    // ========================================
    private static final List<Class<?>> REPRESENTATIVE_MODULE_BEANS = List.of(
            AuthController.class, AuthServiceImpl.class,
            CompanyController.class, CompanyServiceImpl.class,
            WorkflowController.class, WorkflowEngine.class,
            NotificationController.class, NotificationServiceImpl.class,
            AttendanceController.class, AttendanceServiceImpl.class,
            LeaveController.class, LeaveServiceImpl.class,
            SystemConfigController.class, SystemConfigServiceImpl.class,
            AuditLogController.class, AuditLogServiceImpl.class,
            AccountController.class, AccountServiceImpl.class,
            PayrollRecordController.class, PayrollCalculationServiceImpl.class,
            ProjectController.class, ProjectServiceImpl.class,
            DocumentController.class, DocumentServiceImpl.class,
            ReportController.class, ReportServiceImpl.class,
            CrmController.class, CrmServiceImpl.class,
            InventoryController.class, InventoryServiceImpl.class,
            MeetingController.class, MeetingServiceImpl.class,
            AnnouncementController.class, AnnouncementServiceImpl.class);

    // ========================================
    // 輕量 Context 啟動器 / Lightweight Context Runner
    // ========================================
    private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner()
            .withUserConfiguration(Application.class)
            .withPropertyValues(
                    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,"
                            + "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,"
                            + "org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration,"
                            + "org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration,"
                            + "org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration",
                    "spring.main.allow-bean-definition-overriding=true");

    // ========================================
    // 全模組關閉驗證 / All Modules Disabled Verification
    // ========================================
    @Test
    void allModulesDisabledShouldStartWithoutRegisteringModuleBeans() {
        contextRunner.withPropertyValues(disableModules(ALL_MODULES))
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    REPRESENTATIVE_MODULE_BEANS.forEach(beanType ->
                            assertThat(context).doesNotHaveBean(beanType));
                });
    }

    // ========================================
    // 第二階段模組隔離驗證 / Phase Two Module Isolation Verification
    // ========================================
    @Test
    void phaseTwoModulesDisabledShouldRemainAbsentWhenAllOtherModulesAreOffForIsolation() {
        contextRunner
                .withPropertyValues(disableModules(List.of(
                        "notification", "attendance", "leave", "system", "audit", "finance")))
                .withPropertyValues(disableModules(List.of(
                        "auth", "organization", "workflow", "payroll", "project", "document", "report", "crm",
                        "inventory", "meeting", "announcement")))
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).doesNotHaveBean(NotificationController.class);
                    assertThat(context).doesNotHaveBean(AttendanceController.class);
                    assertThat(context).doesNotHaveBean(LeaveController.class);
                    assertThat(context).doesNotHaveBean(SystemConfigController.class);
                    assertThat(context).doesNotHaveBean(AuditLogController.class);
                    assertThat(context).doesNotHaveBean(AccountController.class);
                });
    }

    // ========================================
    // 第四階段模組隔離驗證 / Phase Four Module Isolation Verification
    // ========================================
    @Test
    void phaseFourModulesDisabledShouldRemainAbsentWhenAllOtherModulesAreOffForIsolation() {
        contextRunner
                .withPropertyValues(disableModules(List.of("inventory", "meeting", "announcement")))
                .withPropertyValues(disableModules(List.of(
                        "auth", "organization", "workflow", "notification", "attendance", "leave", "system",
                        "audit", "finance", "payroll", "project", "document", "report", "crm")))
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).doesNotHaveBean(InventoryController.class);
                    assertThat(context).doesNotHaveBean(MeetingController.class);
                    assertThat(context).doesNotHaveBean(AnnouncementController.class);
                });
    }

    // ========================================
    // 模組關閉屬性產生 / Disabled Module Property Generation
    // ========================================
    private static String[] disableModules(List<String> modules) {
        return modules.stream()
                .map(module -> "modules." + module + "=false")
                .toArray(String[]::new);
    }
}
