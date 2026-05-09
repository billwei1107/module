package com.enterprise.system.service.impl;

import com.enterprise.system.dto.FeatureDependencyIssueDTO;
import com.enterprise.system.dto.FeatureInstallationPlanDTO;
import com.enterprise.system.dto.FeatureToggleDTO;
import com.enterprise.system.service.FeatureToggleService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

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

    private static final List<ModuleDefinition> MODULE_DEFINITIONS = List.of(
            new ModuleDefinition("auth", "認證授權", "Authentication", "CORE", "P0",
                    "/login", List.of(), "auth", "classpath:db/migration"),
            new ModuleDefinition("organization", "組織管理", "Organization", "CORE", "P0",
                    "/department", List.of("auth"), "organization", "classpath:db/migration/organization"),
            new ModuleDefinition("workflow", "審批流程", "Workflow", "CORE", "P0",
                    "/workflow", List.of("auth", "organization"), "workflow", "classpath:db/migration/workflow"),
            new ModuleDefinition("notification", "通知中心", "Notification", "OPERATIONS", "P1",
                    null, List.of("auth", "organization"), "notification", "classpath:db/migration/notification"),
            new ModuleDefinition("attendance", "打卡考勤", "Attendance", "OPERATIONS", "P1",
                    "/attendance/clock-in", List.of("auth", "organization"), "attendance", "classpath:db/migration/attendance"),
            new ModuleDefinition("leave", "請假管理", "Leave Management", "OPERATIONS", "P1",
                    "/leave/requests", List.of("auth", "organization", "workflow", "attendance"), "leave", "classpath:db/migration/leave"),
            new ModuleDefinition("system", "系統設定", "System Settings", "OPERATIONS", "P1",
                    "/system", List.of("auth"), "system", "classpath:db/migration/system"),
            new ModuleDefinition("audit", "稽核日誌", "Audit Log", "OPERATIONS", "P1",
                    "/audit/logs", List.of("auth"), "audit", "classpath:db/migration/audit"),
            new ModuleDefinition("finance", "財務管理", "Finance", "OPERATIONS", "P1",
                    "/finance", List.of("auth", "organization", "workflow"), "finance", "classpath:db/migration/finance"),
            new ModuleDefinition("payroll", "薪資管理", "Payroll", "EXTENSION", "P2",
                    "/payroll", List.of("auth", "organization", "attendance", "leave", "finance"), "payroll", "classpath:db/migration/payroll"),
            new ModuleDefinition("project", "專案任務", "Project Management", "EXTENSION", "P2",
                    "/projects", List.of("auth", "organization", "notification"), "project", "classpath:db/migration/project"),
            new ModuleDefinition("document", "文件管理", "Document Management", "EXTENSION", "P2",
                    "/documents", List.of("auth", "organization"), "document", "classpath:db/migration/document"),
            new ModuleDefinition("report", "報表分析", "Report Analytics", "EXTENSION", "P2",
                    "/reports", List.of("auth", "organization"), "report", "classpath:db/migration/report"),
            new ModuleDefinition("crm", "客戶管理", "CRM", "EXTENSION", "P2",
                    "/crm", List.of("auth", "organization"), "crm", "classpath:db/migration/crm"),
            new ModuleDefinition("inventory", "庫存管理", "Inventory", "ADVANCED", "P3",
                    "/inventory", List.of("auth", "organization"), "inventory", "classpath:db/migration/inventory"),
            new ModuleDefinition("meeting", "會議管理", "Meeting Management", "ADVANCED", "P3",
                    "/meetings", List.of("auth", "organization", "notification"), "meeting", "classpath:db/migration/meeting"),
            new ModuleDefinition("announcement", "公告系統", "Announcement", "ADVANCED", "P3",
                    "/announcements", List.of("auth", "organization", "notification"), "announcement", "classpath:db/migration/announcement")
    );
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
