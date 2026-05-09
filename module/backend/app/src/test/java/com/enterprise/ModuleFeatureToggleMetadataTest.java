package com.enterprise;

import com.enterprise.announcement.config.AnnouncementModuleConfig;
import com.enterprise.attendance.config.AttendanceModuleConfig;
import com.enterprise.audit.config.AuditModuleConfig;
import com.enterprise.auth.config.AuthModuleConfig;
import com.enterprise.crm.config.CrmModuleConfig;
import com.enterprise.document.config.DocumentModuleConfig;
import com.enterprise.finance.config.FinanceModuleConfig;
import com.enterprise.inventory.config.InventoryModuleConfig;
import com.enterprise.leave.config.LeaveModuleConfig;
import com.enterprise.meeting.config.MeetingModuleConfig;
import com.enterprise.notification.config.NotificationModuleConfig;
import com.enterprise.notification.config.WebSocketConfig;
import com.enterprise.organization.config.OrganizationModuleConfig;
import com.enterprise.payroll.config.PayrollModuleConfig;
import com.enterprise.project.config.ProjectModuleConfig;
import com.enterprise.report.config.ReportModuleConfig;
import com.enterprise.system.config.SystemModuleConfig;
import com.enterprise.workflow.config.WorkflowModuleConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @file ModuleFeatureToggleMetadataTest.java
 * @description 模組啟停元資料測試 / Module feature toggle metadata tests
 * @description_en Verifies module config classes and app scan boundaries for feature toggles
 * @description_zh 驗證模組配置類與 app 掃描邊界，避免模組關閉時仍被全域掃描載入
 */
class ModuleFeatureToggleMetadataTest {
    private static final List<ModuleConfig> MODULE_CONFIGS = List.of(
            new ModuleConfig("auth", AuthModuleConfig.class),
            new ModuleConfig("organization", OrganizationModuleConfig.class),
            new ModuleConfig("workflow", WorkflowModuleConfig.class),
            new ModuleConfig("notification", NotificationModuleConfig.class),
            new ModuleConfig("attendance", AttendanceModuleConfig.class),
            new ModuleConfig("leave", LeaveModuleConfig.class),
            new ModuleConfig("system", SystemModuleConfig.class),
            new ModuleConfig("audit", AuditModuleConfig.class),
            new ModuleConfig("finance", FinanceModuleConfig.class),
            new ModuleConfig("payroll", PayrollModuleConfig.class),
            new ModuleConfig("project", ProjectModuleConfig.class),
            new ModuleConfig("document", DocumentModuleConfig.class),
            new ModuleConfig("report", ReportModuleConfig.class),
            new ModuleConfig("crm", CrmModuleConfig.class),
            new ModuleConfig("inventory", InventoryModuleConfig.class),
            new ModuleConfig("meeting", MeetingModuleConfig.class),
            new ModuleConfig("announcement", AnnouncementModuleConfig.class));

    @Test
    void moduleConfigsShouldDeclareMatchingFeatureToggleProperties() {
        String applicationYaml = readApplicationYaml();
        MODULE_CONFIGS.forEach(moduleConfig -> {
            ConditionalOnProperty condition = moduleConfig.configClass().getAnnotation(ConditionalOnProperty.class);
            assertThat(condition)
                    .as("%s must be guarded by @ConditionalOnProperty", moduleConfig.configClass().getSimpleName())
                    .isNotNull();
            assertThat(propertyName(condition)).isEqualTo("modules." + moduleConfig.moduleName());
            assertThat(condition.havingValue()).isEqualTo("true");
            assertThat(condition.matchIfMissing()).isTrue();
            assertThat(applicationYaml).contains("\n  " + moduleConfig.moduleName() + ":");
        });
    }

    @Test
    void notificationWebSocketConfigShouldFollowNotificationToggle() {
        ConditionalOnProperty condition = WebSocketConfig.class.getAnnotation(ConditionalOnProperty.class);
        assertThat(condition).isNotNull();
        assertThat(propertyName(condition)).isEqualTo("modules.notification");
    }

    @Test
    void applicationShouldScanOnlyCommonAndModuleConfigPackages() {
        SpringBootApplication application = Application.class.getAnnotation(SpringBootApplication.class);
        assertThat(application).isNotNull();
        List<String> scanPackages = Arrays.asList(application.scanBasePackages());
        assertThat(scanPackages).contains("com.enterprise.common");
        assertThat(scanPackages).allSatisfy(scanPackage ->
                assertThat(scanPackage).satisfiesAnyOf(
                        value -> assertThat(value).isEqualTo("com.enterprise.common"),
                        value -> assertThat(value).endsWith(".config")));
        assertThat(scanPackages).doesNotContain("com.enterprise");
    }

    private static String propertyName(ConditionalOnProperty condition) {
        String prefix = condition.prefix();
        String name = condition.name().length > 0 ? condition.name()[0] : condition.value()[0];
        return prefix == null || prefix.isBlank() ? name : prefix + "." + name;
    }

    private static String readApplicationYaml() {
        try (var stream = ModuleFeatureToggleMetadataTest.class.getResourceAsStream("/application.yml")) {
            if (stream == null) {
                throw new IllegalStateException("application.yml not found");
            }
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

    private record ModuleConfig(String moduleName, Class<?> configClass) {
    }
}
