/**
 * @file auditApi.ts
 * @description 稽核日誌 API 請求層 / Audit log API request layer
 * @description_en Wraps audit log search and CSV export endpoints
 * @description_zh 封裝稽核日誌查詢與 CSV 匯出 REST 呼叫
 */

import axiosInstance from '../../../shared/api/axiosInstance';
import type { AuditLogPage, AuditLogQuery } from '../types';

interface ApiEnvelope<T> {
    data: T;
}

const unwrapData = <T>(response: ApiEnvelope<T>): T => {
    if (!response || response.data === undefined) {
        throw new Error('Invalid API response');
    }
    return response.data;
};

export const getAuditLogs = async (query: AuditLogQuery = {}): Promise<AuditLogPage> => {
    const response = await axiosInstance.get('/api/v1/audit/logs', { params: query });
    return unwrapData<AuditLogPage>(response as ApiEnvelope<AuditLogPage>);
};

export const exportAuditLogsCsv = async (query: AuditLogQuery = {}): Promise<string> => {
    const response = await axiosInstance.get('/api/v1/audit/logs/export', {
        params: query,
        responseType: 'text',
    });
    return response as unknown as string;
};
