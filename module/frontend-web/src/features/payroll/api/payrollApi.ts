/**
 * @file payrollApi.ts
 * @description 薪資管理 API 請求層 / Payroll API request layer
 * @description_en Wraps salary structure and payroll record endpoints
 * @description_zh 封裝薪資結構與薪資紀錄 REST 呼叫
 */

import axiosInstance from '../../../shared/api/axiosInstance';
import type { PayrollRecord, SalaryStructure } from '../types';

interface ApiEnvelope<T> {
    data: T;
}

const unwrapData = <T>(response: ApiEnvelope<T>): T => {
    if (!response || response.data === undefined) {
        throw new Error('Invalid API response');
    }
    return response.data;
};

export const getSalaryStructures = async (): Promise<SalaryStructure[]> => {
    const response = await axiosInstance.get('/api/v1/payroll/salary-structures');
    return unwrapData<SalaryStructure[]>(response as ApiEnvelope<SalaryStructure[]>);
};

export const upsertSalaryStructure = async (data: {
    name: string;
    employeeId: string;
    type: string;
    baseSalary: number;
    hourlyRate: number;
}): Promise<SalaryStructure> => {
    const response = await axiosInstance.put('/api/v1/payroll/salary-structures', data);
    return unwrapData<SalaryStructure>(response as ApiEnvelope<SalaryStructure>);
};

export const calculatePayroll = async (employeeId: string, month: string): Promise<PayrollRecord> => {
    const response = await axiosInstance.post('/api/v1/payroll/calculate', null, {
        params: { employeeId, month },
    });
    return unwrapData<PayrollRecord>(response as ApiEnvelope<PayrollRecord>);
};

export const getPayrollRecords = async (): Promise<PayrollRecord[]> => {
    const response = await axiosInstance.get('/api/v1/payroll/records');
    return unwrapData<PayrollRecord[]>(response as ApiEnvelope<PayrollRecord[]>);
};
