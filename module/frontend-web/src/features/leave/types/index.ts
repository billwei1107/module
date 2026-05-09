/**
 * @file index.ts
 * @description 請假管理型別定義 / Leave management type definitions
 * @description_zh 定義假別、配額與請假申請資料結構
 */

export interface LeaveType {
    id: string;
    name: string;
    code: string;
    annualQuotaHours: number;
    requiresApproval: boolean;
    paid: boolean;
    active: boolean;
}

export interface LeaveBalance {
    id: string;
    employeeId: string;
    leaveTypeId: string;
    year: number;
    totalHours: number;
    usedHours: number;
    reservedHours: number;
    availableHours: number;
}

export interface LeaveRequest {
    id: string;
    employeeId: string;
    leaveTypeId: string;
    startTime: string;
    endTime: string;
    hours: number;
    reason: string;
    status: 'PENDING' | 'APPROVED' | 'REJECTED';
    workflowInstanceId?: string;
}

export interface CreateLeaveRequest {
    employeeId?: string;
    leaveTypeId: string;
    startTime: string;
    endTime: string;
    reason: string;
}
