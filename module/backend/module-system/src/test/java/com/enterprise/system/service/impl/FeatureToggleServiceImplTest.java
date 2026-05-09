package com.enterprise.system.service.impl;

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
}
