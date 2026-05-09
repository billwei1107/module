/**
 * @file index.ts
 * @description 稽核日誌型別定義 / Audit log type definitions
 * @description_en Defines audit log search and response data structures
 * @description_zh 定義稽核日誌查詢與回傳資料結構
 */

export interface AuditLog {
    id: string;
    userId?: string;
    userName?: string;
    module: string;
    action: string;
    resourceType?: string;
    resourceId?: string;
    requestMethod?: string;
    requestUrl?: string;
    responseStatus?: number;
    ipAddress?: string;
    executionTimeMs?: number;
    createdAt?: string;
}

export interface AuditLogPage {
    content: AuditLog[];
    pageNumber: number;
    pageSize: number;
    totalElements: number;
    totalPages: number;
    last: boolean;
}

export interface AuditLogQuery {
    module?: string;
    action?: string;
    userId?: string;
    resourceType?: string;
    page?: number;
    size?: number;
}
