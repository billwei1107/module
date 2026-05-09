/**
 * @file index.ts
 * @description 報表分析型別定義 / Report type definitions
 * @description_en Defines report module API data structures for smoke verification UI
 * @description_zh 定義報表分析模組驗證頁使用的 API 資料結構
 */

export interface ReportDefinition {
    id: string;
    name: string;
    dataSourceSql: string;
    columnsJson: string;
    filtersJson: string;
    active: boolean;
}

export interface ReportExecutionResult {
    definitionId: string;
    columns: string[];
    rows: Record<string, string | number | boolean | null>[];
    rowCount: number;
}

export interface Widget {
    id: string;
    dashboardId: string;
    title: string;
    type: 'BAR' | 'LINE' | 'PIE' | 'NUMBER';
    dataSourceSql: string;
    positionJson: string;
}

export interface Dashboard {
    id: string;
    name: string;
    ownerId?: string;
    layoutJson: string;
    active: boolean;
    widgets: Widget[];
}

export interface ReportSchedule {
    id: string;
    definitionId: string;
    cronExpression: string;
    recipientEmails: string;
    lastRunAt?: string;
    active: boolean;
}

export interface BusinessSummary {
    attendanceRecords: number;
    overtimeMinutes: number;
    openInvoiceAmount: number;
    payrollNetPay: number;
}
