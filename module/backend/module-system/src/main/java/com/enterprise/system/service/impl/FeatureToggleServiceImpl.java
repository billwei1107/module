package com.enterprise.system.service.impl;

import com.enterprise.system.dto.FeatureDependencyIssueDTO;
import com.enterprise.system.dto.FeatureInstallationPlanDTO;
import com.enterprise.system.dto.FeatureToggleDTO;
import com.enterprise.system.service.FeatureToggleService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @file FeatureToggleServiceImpl.java
 * @description 功能開關與模組清冊服務實作 / Feature toggle and module catalog service implementation
 * @description_zh 從 modules.* 設定讀取各模組啟用狀態，並回傳可供專案移植參考的模組清冊
 */
@Service
@RequiredArgsConstructor
public class FeatureToggleServiceImpl implements FeatureToggleService {

    private static final String MODULE_CATALOG_RESOURCE = "module-catalog.tsv";
    private static final int MODULE_CATALOG_COLUMN_COUNT = 9;
    private static final List<ModuleDefinition> MODULE_DEFINITIONS = loadModuleDefinitions();
    private static final Map<String, ModuleDefinition> MODULE_DEFINITION_MAP = MODULE_DEFINITIONS.stream()
            .collect(LinkedHashMap::new, (map, definition) -> map.put(definition.module(), definition), LinkedHashMap::putAll);

    private final Environment environment;

    @Override
    public List<FeatureToggleDTO> getFeatures() {
        return MODULE_DEFINITIONS.stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public List<FeatureDependencyIssueDTO> getDependencyIssues() {
        return MODULE_DEFINITIONS.stream()
                .filter(definition -> isEnabled(definition.module()))
                .map(this::toDependencyIssue)
                .filter(issue -> !issue.getMissingDependencies().isEmpty())
                .toList();
    }

    @Override
    public FeatureInstallationPlanDTO createInstallationPlan(List<String> selectedModules) {
        List<String> requestedModules = normalizeModules(selectedModules);
        Set<String> requiredModules = new LinkedHashSet<>();
        Set<String> unknownModules = new LinkedHashSet<>();

        requestedModules.forEach(module -> collectDependencies(module, requiredModules, unknownModules));

        List<String> additionalModules = requiredModules.stream()
                .filter(module -> !requestedModules.contains(module))
                .toList();
        List<FeatureToggleDTO> modules = requiredModules.stream()
                .map(MODULE_DEFINITION_MAP::get)
                .map(this::toDto)
                .toList();

        return FeatureInstallationPlanDTO.builder()
                .requestedModules(requestedModules)
                .requiredModules(requiredModules.stream().toList())
                .additionalModules(additionalModules)
                .unknownModules(unknownModules.stream().toList())
                .backendModules(modules.stream().map(FeatureToggleDTO::getBackendModule).toList())
                .frontendFeatures(modules.stream().map(FeatureToggleDTO::getFrontendFeature).toList())
                .flywayLocations(modules.stream().map(FeatureToggleDTO::getFlywayLocation).distinct().toList())
                .defaultPaths(modules.stream().map(FeatureToggleDTO::getDefaultPath).filter(path -> path != null).toList())
                .modules(modules)
                .build();
    }

    @Override
    public FeatureInstallationPlanDTO getCurrentInstallationPlan() {
        List<String> enabledModules = MODULE_DEFINITIONS.stream()
                .map(ModuleDefinition::module)
                .filter(this::isEnabled)
                .toList();
        return createInstallationPlan(enabledModules);
    }

    // ========================================
    // 清冊轉換 / Catalog Mapping
    // ========================================
    private FeatureToggleDTO toDto(ModuleDefinition definition) {
        return FeatureToggleDTO.builder()
                .module(definition.module())
                .enabled(environment.getProperty("modules." + definition.module(), Boolean.class, false))
                .displayName(definition.displayName())
                .displayNameEn(definition.displayNameEn())
                .phase(definition.phase())
                .priority(definition.priority())
                .backendModule("module/backend/module-" + definition.sourceKey())
                .frontendFeature("module/frontend-web/src/features/" + definition.sourceKey())
                .flywayLocation(definition.flywayLocation())
                .defaultPath(definition.defaultPath())
                .dependencies(definition.dependencies())
                .build();
    }

    // ========================================
    // 依賴檢查 / Dependency Validation
    // ========================================
    private FeatureDependencyIssueDTO toDependencyIssue(ModuleDefinition definition) {
        List<String> missingDependencies = definition.dependencies().stream()
                .filter(dependency -> !isEnabled(dependency))
                .toList();
        return FeatureDependencyIssueDTO.builder()
                .module(definition.module())
                .displayName(definition.displayName())
                .missingDependencies(missingDependencies)
                .message(buildDependencyMessage(definition, missingDependencies))
                .build();
    }

    private boolean isEnabled(String module) {
        return environment.getProperty("modules." + module, Boolean.class, false);
    }

    // ========================================
    // 安裝計畫 / Installation Planning
    // ========================================
    private List<String> normalizeModules(List<String> modules) {
        if (modules == null) {
            return List.of();
        }
        return modules.stream()
                .filter(module -> module != null && !module.isBlank())
                .map(module -> module.trim().toLowerCase())
                .distinct()
                .toList();
    }

    private void collectDependencies(String module, Set<String> requiredModules, Set<String> unknownModules) {
        ModuleDefinition definition = MODULE_DEFINITION_MAP.get(module);
        if (definition == null) {
            unknownModules.add(module);
            return;
        }
        definition.dependencies().forEach(dependency -> collectDependencies(dependency, requiredModules, unknownModules));
        requiredModules.add(module);
    }

    private String buildDependencyMessage(ModuleDefinition definition, List<String> missingDependencies) {
        if (missingDependencies.isEmpty()) {
            return "";
        }
        return definition.module() + " requires enabled modules: " + String.join(", ", missingDependencies);
    }

    // ========================================
    // 模組清冊讀取 / Module Catalog Loading
    // ========================================
    private static List<ModuleDefinition> loadModuleDefinitions() {
        InputStream stream = FeatureToggleServiceImpl.class.getClassLoader()
                .getResourceAsStream(MODULE_CATALOG_RESOURCE);
        if (stream == null) {
            throw new IllegalStateException("Module catalog resource not found: " + MODULE_CATALOG_RESOURCE);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            return reader.lines()
                    .filter(line -> !line.isBlank() && !line.startsWith("#"))
                    .map(FeatureToggleServiceImpl::toModuleDefinition)
                    .toList();
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to read module catalog: " + MODULE_CATALOG_RESOURCE, exception);
        }
    }

    private static ModuleDefinition toModuleDefinition(String line) {
        String[] columns = line.split("\t", -1);
        if (columns.length != MODULE_CATALOG_COLUMN_COUNT) {
            throw new IllegalStateException("Invalid module catalog row: " + line);
        }

        return new ModuleDefinition(
                columns[0],
                columns[1],
                columns[2],
                columns[3],
                columns[4],
                parseNullable(columns[5]),
                parseDependencies(columns[6]),
                columns[7],
                columns[8]
        );
    }

    private static String parseNullable(String value) {
        return "-".equals(value) || value.isBlank() ? null : value;
    }

    private static List<String> parseDependencies(String value) {
        if ("-".equals(value) || value.isBlank()) {
            return List.of();
        }
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(dependency -> !dependency.isBlank())
                .toList();
    }

    private record ModuleDefinition(
            String module,
            String displayName,
            String displayNameEn,
            String phase,
            String priority,
            String defaultPath,
            List<String> dependencies,
            String sourceKey,
            String flywayLocation
    ) {
    }
}
