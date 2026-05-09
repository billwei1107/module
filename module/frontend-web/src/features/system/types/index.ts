/**
 * @file index.ts
 * @description 系統設定型別定義 / System settings type definitions
 * @description_zh 定義系統設定、功能開關、資料字典與流水號資料結構
 */

export interface SystemConfig {
    id: string;
    key: string;
    value: string;
    category: string;
    description?: string;
}

export interface FeatureToggle {
    module: string;
    enabled: boolean;
    displayName?: string;
    displayNameEn?: string;
    phase?: string;
    priority?: string;
    backendModule?: string;
    frontendFeature?: string;
    flywayLocation?: string;
    defaultPath?: string;
    dependencies?: string[];
}

export interface FeatureDependencyIssue {
    module: string;
    displayName: string;
    missingDependencies: string[];
    message: string;
}

export interface CreateFeatureInstallationPlanRequest {
    selectedModules: string[];
}

export interface FeatureInstallationPlan {
    requestedModules: string[];
    requiredModules: string[];
    additionalModules: string[];
    unknownModules: string[];
    backendModules: string[];
    frontendFeatures: string[];
    flywayLocations: string[];
    defaultPaths: string[];
    modules: FeatureToggle[];
}

export interface DictionaryItem {
    id: string;
    dictionaryId: string;
    label: string;
    value: string;
    sortOrder: number;
    active: boolean;
}

export interface Dictionary {
    id: string;
    code: string;
    name: string;
    active: boolean;
    items: DictionaryItem[];
}

export interface UpdateSystemConfigRequest {
    key: string;
    value: string;
    category: string;
    description?: string;
}
