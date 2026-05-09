/**
 * @file financeApi.ts
 * @description 財務管理 API 請求層 / Finance API request layer
 * @description_en Wraps finance account, journal, invoice aging, and budget endpoints
 * @description_zh 封裝財務科目、傳票、帳齡與預算 REST 呼叫
 */

import axiosInstance from '../../../shared/api/axiosInstance';
import type { Account, AgingBucket, Budget, JournalEntry, JournalLine } from '../types';

interface ApiEnvelope<T> {
    data: T;
}

const unwrapData = <T>(response: ApiEnvelope<T>): T => {
    if (!response || response.data === undefined) {
        throw new Error('Invalid API response');
    }
    return response.data;
};

export const getAccounts = async (): Promise<Account[]> => {
    const response = await axiosInstance.get('/api/v1/finance/accounts');
    return unwrapData<Account[]>(response as ApiEnvelope<Account[]>);
};

export const createAccount = async (data: {
    code: string;
    name: string;
    type: string;
    level: number;
}): Promise<Account> => {
    const response = await axiosInstance.post('/api/v1/finance/accounts', data);
    return unwrapData<Account>(response as ApiEnvelope<Account>);
};

export const getJournalEntries = async (): Promise<JournalEntry[]> => {
    const response = await axiosInstance.get('/api/v1/finance/journal-entries');
    return unwrapData<JournalEntry[]>(response as ApiEnvelope<JournalEntry[]>);
};

export const createJournalEntry = async (data: {
    entryNo: string;
    date: string;
    description: string;
    lines: JournalLine[];
}): Promise<JournalEntry> => {
    const response = await axiosInstance.post('/api/v1/finance/journal-entries', data);
    return unwrapData<JournalEntry>(response as ApiEnvelope<JournalEntry>);
};

export const getAgingAnalysis = async (): Promise<AgingBucket[]> => {
    const response = await axiosInstance.get('/api/v1/finance/invoices/aging');
    return unwrapData<AgingBucket[]>(response as ApiEnvelope<AgingBucket[]>);
};

export const getBudgets = async (): Promise<Budget[]> => {
    const response = await axiosInstance.get('/api/v1/finance/budgets');
    return unwrapData<Budget[]>(response as ApiEnvelope<Budget[]>);
};
