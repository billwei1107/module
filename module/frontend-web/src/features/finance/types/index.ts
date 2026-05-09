/**
 * @file index.ts
 * @description 財務管理型別定義 / Finance type definitions
 * @description_en Defines finance module API data structures for smoke verification UI
 * @description_zh 定義財務模組驗證頁使用的 API 資料結構
 */

export type AccountType = 'ASSET' | 'LIABILITY' | 'EQUITY' | 'REVENUE' | 'EXPENSE';
export type JournalStatus = 'DRAFT' | 'POSTED' | 'VOIDED';

export interface Account {
    id: string;
    code: string;
    name: string;
    type: AccountType;
    level: number;
    active: boolean;
}

export interface JournalLine {
    accountId: string;
    debitAmount: number;
    creditAmount: number;
    description?: string;
}

export interface JournalEntry {
    id: string;
    entryNo: string;
    date: string;
    description?: string;
    status: JournalStatus;
    totalDebit: number;
    totalCredit: number;
    lines: JournalLine[];
}

export interface AgingBucket {
    bucket: string;
    amount: number;
}

export interface Budget {
    id: string;
    name: string;
    departmentId?: string;
    fiscalYear: number;
    totalAmount: number;
    actualAmount: number;
    usageRate: number;
    warning: boolean;
}
