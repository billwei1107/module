/**
 * @file leaveApi.ts
 * @description 請假管理 API 請求層 / Leave management API request layer
 * @description_zh 封裝假別、配額、請假申請與審核 REST 呼叫
 */

import axiosInstance from '../../../shared/api/axiosInstance';
import type { CreateLeaveRequest, LeaveBalance, LeaveRequest, LeaveType } from '../types';

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
// 假別與配額 / Leave Types and Balances
// ========================================
export const getLeaveTypes = async (): Promise<LeaveType[]> => {
    const response = await axiosInstance.get('/api/v1/leaves/types');
    return unwrapData<LeaveType[]>(response as ApiEnvelope<LeaveType[]>);
};

export const getLeaveBalances = async (employeeId: string, year: number): Promise<LeaveBalance[]> => {
    const response = await axiosInstance.get('/api/v1/leaves/balances', {
        params: { employeeId, year },
    });
    return unwrapData<LeaveBalance[]>(response as ApiEnvelope<LeaveBalance[]>);
};

// ========================================
// 請假申請 / Leave Requests
// ========================================
export const submitLeaveRequest = async (data: CreateLeaveRequest): Promise<LeaveRequest> => {
    const response = await axiosInstance.post('/api/v1/leaves/requests', data);
    return unwrapData<LeaveRequest>(response as ApiEnvelope<LeaveRequest>);
};

export const getLeaveRequests = async (employeeId: string): Promise<LeaveRequest[]> => {
    const response = await axiosInstance.get('/api/v1/leaves/requests', {
        params: { employeeId },
    });
    return unwrapData<LeaveRequest[]>(response as ApiEnvelope<LeaveRequest[]>);
};

export const getPendingLeaveRequests = async (): Promise<LeaveRequest[]> => {
    const response = await axiosInstance.get('/api/v1/leaves/requests/pending');
    return unwrapData<LeaveRequest[]>(response as ApiEnvelope<LeaveRequest[]>);
};

export const approveLeaveRequest = async (id: string): Promise<LeaveRequest> => {
    const response = await axiosInstance.post(`/api/v1/leaves/requests/${id}/approve`);
    return unwrapData<LeaveRequest>(response as ApiEnvelope<LeaveRequest>);
};

export const rejectLeaveRequest = async (id: string): Promise<LeaveRequest> => {
    const response = await axiosInstance.post(`/api/v1/leaves/requests/${id}/reject`);
    return unwrapData<LeaveRequest>(response as ApiEnvelope<LeaveRequest>);
};
