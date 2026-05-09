/**
 * @file reportApi.ts
 * @description 報表分析 API 請求層 / Report API request layer
 * @description_en Wraps report definition, execution, dashboard, schedule, and summary endpoints
 * @description_zh 封裝報表定義、執行、儀表板、排程與統計 REST 呼叫
 */

import axiosInstance from '../../../shared/api/axiosInstance';
import type { BusinessSummary, Dashboard, ReportDefinition, ReportExecutionResult, ReportSchedule, Widget } from '../types';

interface ApiEnvelope<T> {
    data: T;
}

const unwrapData = <T>(response: ApiEnvelope<T>): T => {
    if (!response || response.data === undefined) {
        throw new Error('Invalid API response');
    }
    return response.data;
};

export const getReportDefinitions = async (): Promise<ReportDefinition[]> => {
    const response = await axiosInstance.get('/api/v1/reports/definitions');
    return unwrapData<ReportDefinition[]>(response as ApiEnvelope<ReportDefinition[]>);
};

export const createReportDefinition = async (data: {
    name: string;
    dataSourceSql: string;
    columnsJson: string;
    filtersJson: string;
}): Promise<ReportDefinition> => {
    const response = await axiosInstance.post('/api/v1/reports/definitions', data);
    return unwrapData<ReportDefinition>(response as ApiEnvelope<ReportDefinition>);
};

export const executeReport = async (definitionId: string): Promise<ReportExecutionResult> => {
    const response = await axiosInstance.post(`/api/v1/reports/definitions/${definitionId}/execute`);
    return unwrapData<ReportExecutionResult>(response as ApiEnvelope<ReportExecutionResult>);
};

export const getDashboards = async (): Promise<Dashboard[]> => {
    const response = await axiosInstance.get('/api/v1/reports/dashboards');
    return unwrapData<Dashboard[]>(response as ApiEnvelope<Dashboard[]>);
};

export const createDashboard = async (data: { name: string; ownerId: string; layoutJson: string }): Promise<Dashboard> => {
    const response = await axiosInstance.post('/api/v1/reports/dashboards', data);
    return unwrapData<Dashboard>(response as ApiEnvelope<Dashboard>);
};

export const createWidget = async (
    dashboardId: string,
    data: { title: string; type: 'BAR' | 'LINE' | 'PIE' | 'NUMBER'; dataSourceSql: string; positionJson: string },
): Promise<Widget> => {
    const response = await axiosInstance.post(`/api/v1/reports/dashboards/${dashboardId}/widgets`, data);
    return unwrapData<Widget>(response as ApiEnvelope<Widget>);
};

export const createReportSchedule = async (data: {
    definitionId: string;
    cronExpression: string;
    recipientEmails: string;
}): Promise<ReportSchedule> => {
    const response = await axiosInstance.post('/api/v1/reports/schedules', data);
    return unwrapData<ReportSchedule>(response as ApiEnvelope<ReportSchedule>);
};

export const getBusinessSummary = async (): Promise<BusinessSummary> => {
    const response = await axiosInstance.get('/api/v1/reports/summary');
    return unwrapData<BusinessSummary>(response as ApiEnvelope<BusinessSummary>);
};
