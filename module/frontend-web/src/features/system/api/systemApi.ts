/**
 * @file systemApi.ts
 * @description 系統設定 API 請求層 / System settings API request layer
 * @description_zh 封裝設定、功能開關、資料字典與流水號 REST 呼叫
 */

import axiosInstance from '../../../shared/api/axiosInstance';
import type {
    CreateFeatureInstallationPlanRequest,
    Dictionary,
    FeatureDependencyIssue,
    FeatureInstallationPlan,
    FeatureToggle,
    SystemConfig,
    UpdateSystemConfigRequest,
} from '../types';

interface ApiEnvelope<T> {
    data: T;
}

const unwrapData = <T>(response: ApiEnvelope<T>): T => {
    if (!response || response.data === undefined) {
        throw new Error('Invalid API response');
    }
    return response.data;
};

// ========================================
// 系統設定 / System Configs
// ========================================
export const getSystemConfigs = async (): Promise<SystemConfig[]> => {
    const response = await axiosInstance.get('/api/v1/system/configs');
    return unwrapData<SystemConfig[]>(response as ApiEnvelope<SystemConfig[]>);
};

export const upsertSystemConfig = async (data: UpdateSystemConfigRequest): Promise<SystemConfig> => {
    const response = await axiosInstance.put('/api/v1/system/configs', data);
    return unwrapData<SystemConfig>(response as ApiEnvelope<SystemConfig>);
};

// ========================================
// 功能開關 / Feature Toggles
// ========================================
export const getFeatureToggles = async (): Promise<FeatureToggle[]> => {
    const response = await axiosInstance.get('/api/v1/system/features');
    return unwrapData<FeatureToggle[]>(response as ApiEnvelope<FeatureToggle[]>);
};

export const getFeatureDependencyIssues = async (): Promise<FeatureDependencyIssue[]> => {
    const response = await axiosInstance.get('/api/v1/system/features/dependency-issues');
    return unwrapData<FeatureDependencyIssue[]>(response as ApiEnvelope<FeatureDependencyIssue[]>);
};

export const getCurrentFeatureInstallationPlan = async (): Promise<FeatureInstallationPlan> => {
    const response = await axiosInstance.get('/api/v1/system/features/installation-plan/current');
    return unwrapData<FeatureInstallationPlan>(response as ApiEnvelope<FeatureInstallationPlan>);
};

export const createFeatureInstallationPlan = async (
    data: CreateFeatureInstallationPlanRequest
): Promise<FeatureInstallationPlan> => {
    const response = await axiosInstance.post('/api/v1/system/features/installation-plan', data);
    return unwrapData<FeatureInstallationPlan>(response as ApiEnvelope<FeatureInstallationPlan>);
};

// ========================================
// 資料字典 / Dictionaries
// ========================================
export const getDictionaries = async (): Promise<Dictionary[]> => {
    const response = await axiosInstance.get('/api/v1/system/dictionaries');
    return unwrapData<Dictionary[]>(response as ApiEnvelope<Dictionary[]>);
};

export const getNextSequence = async (name: string): Promise<string> => {
    const response = await axiosInstance.post(`/api/v1/system/sequences/${name}/next`);
    return unwrapData<string>(response as ApiEnvelope<string>);
};
