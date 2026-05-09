/**
 * @file index.ts
 * @description 薪資管理型別定義 / Payroll type definitions
 * @description_en Defines payroll module API data structures for smoke verification UI
 * @description_zh 定義薪資模組驗證頁使用的 API 資料結構
 */

export interface SalaryStructure {
    id: string;
    name: string;
    employeeId: string;
    type: 'MONTHLY' | 'HOURLY' | 'DAILY';
    baseSalary: number;
    hourlyRate: number;
    active: boolean;
}

export interface PayrollDetail {
    id?: string;
    itemCode: string;
    itemName: string;
    category: string;
    amount: number;
    description?: string;
}

export interface PayrollRecord {
    id: string;
    employeeId: string;
    yearMonth: string;
    baseSalary: number;
    totalEarnings: number;
    totalDeductions: number;
    netPay: number;
    status: 'DRAFT' | 'CONFIRMED' | 'PAID';
    details: PayrollDetail[];
}
