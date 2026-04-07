import axiosInstance from '../../../shared/api/axiosInstance';
import type { LoginRequest, LoginResponse } from '../types';

export const loginApi = async (data: LoginRequest): Promise<LoginResponse> => {
    const response = await axiosInstance.post('/api/v1/auth/login', data);
    return response.data.data;
};

export const logoutApi = async (): Promise<void> => {
    await axiosInstance.post('/api/v1/auth/logout');
};
