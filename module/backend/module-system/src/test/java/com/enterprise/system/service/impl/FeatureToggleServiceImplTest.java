package com.enterprise.system.service.impl;

import com.enterprise.system.dto.FeatureDependencyIssueDTO;
import com.enterprise.system.dto.FeatureToggleDTO;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @file FeatureToggleServiceImplTest.java
 * @description 功能開關服務測試 / Feature toggle service tests
 */
class FeatureToggleServiceImplTest {

    @Test
    void getFeaturesShouldReadModuleProperties() {
        MockEnvironment environment = new MockEnvironment()
                .withProperty("modules.auth", "true")
                .withProperty("modules.leave", "false")
                .withProperty("modules.system", "true");

        FeatureToggleServiceImpl service = new FeatureToggleServiceImpl(environment);
        List<FeatureToggleDTO> features = service.getFeatures();

        assertThat(features).anySatisfy(feature -> {
            assertThat(feature.getModule()).isEqualTo("auth");
            assertThat(feature.getEnabled()).isTrue();
        });
        assertThat(features).anySatisfy(feature -> {
            assertThat(feature.getModule()).isEqualTo("leave");
            assertThat(feature.getEnabled()).isFalse();
        });
        assertThat(features).anySatisfy(feature -> {
            assertThat(feature.getModule()).isEqualTo("system");
            assertThat(feature.getEnabled()).isTrue();
        });
    }

    @Test
    void getFeaturesShouldExposeReusableModuleCatalogMetadata() {
        MockEnvironment environment = new MockEnvironment()
                .withProperty("modules.payroll", "true")
                .withProperty("modules.notification", "false");

        FeatureToggleServiceImpl service = new FeatureToggleServiceImpl(environment);
        List<FeatureToggleDTO> features = service.getFeatures();

        assertThat(features).hasSize(17);
        assertThat(features).allSatisfy(feature -> {
            assertThat(feature.getDisplayName()).isNotBlank();
            assertThat(feature.getDisplayNameEn()).isNotBlank();
            assertThat(feature.getPhase()).isIn("CORE", "OPERATIONS", "EXTENSION", "ADVANCED");
            assertThat(feature.getPriority()).matches("P[0-3]");
            assertThat(feature.getBackendModule()).startsWith("module/backend/module-");
            assertThat(feature.getFrontendFeature()).startsWith("module/frontend-web/src/features/");
            assertThat(feature.getFlywayLocation()).startsWith("classpath:db/migration");
            assertThat(feature.getDependencies()).isNotNull();
        });
        assertThat(features).anySatisfy(feature -> {
            assertThat(feature.getModule()).isEqualTo("payroll");
            assertThat(feature.getEnabled()).isTrue();
            assertThat(feature.getDefaultPath()).isEqualTo("/payroll");
            assertThat(feature.getDependencies()).containsExactly("auth", "organization", "attendance", "leave", "finance");
        });
        assertThat(features).anySatisfy(feature -> {
            assertThat(feature.getModule()).isEqualTo("notification");
            assertThat(feature.getEnabled()).isFalse();
            assertThat(feature.getDefaultPath()).isNull();
        });
        assertThat(features).anySatisfy(feature -> {
            assertThat(feature.getModule()).isEqualTo("auth");
            assertThat(feature.getFlywayLocation()).isEqualTo("classpath:db/migration");
        });
    }

    @Test
    void getDependencyIssuesShouldReportEnabledModulesWithDisabledDependencies() {
        MockEnvironment environment = new MockEnvironment()
                .withProperty("modules.auth", "true")
                .withProperty("modules.organization", "true")
                .withProperty("modules.payroll", "true")
                .withProperty("modules.attendance", "false")
                .withProperty("modules.leave", "false")
                .withProperty("modules.finance", "false");

        FeatureToggleServiceImpl service = new FeatureToggleServiceImpl(environment);
        List<FeatureDependencyIssueDTO> issues = service.getDependencyIssues();

        assertThat(issues).singleElement().satisfies(issue -> {
            assertThat(issue.getModule()).isEqualTo("payroll");
            assertThat(issue.getDisplayName()).isEqualTo("薪資管理");
            assertThat(issue.getMissingDependencies()).containsExactly("attendance", "leave", "finance");
            assertThat(issue.getMessage()).contains("payroll requires enabled modules");
        });
    }

    @Test
    void getDependencyIssuesShouldReturnEmptyListWhenEnabledModulesAreConsistent() {
        MockEnvironment environment = new MockEnvironment()
                .withProperty("modules.auth", "true")
                .withProperty("modules.organization", "true")
                .withProperty("modules.workflow", "true")
                .withProperty("modules.attendance", "true")
                .withProperty("modules.leave", "true")
                .withProperty("modules.finance", "true")
                .withProperty("modules.payroll", "true");

        FeatureToggleServiceImpl service = new FeatureToggleServiceImpl(environment);

        assertThat(service.getDependencyIssues()).isEmpty();
    }
}
