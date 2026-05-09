import axiosInstance from '../../../shared/api/axiosInstance';
import type { ApiResponse } from '../../../shared/types';
import type { LoginRequest, LoginResponse } from '../types';

/**
 * @file authApi.ts
 * @description 認證 API 請求層 / Authentication API request layer
 * @description_en Wraps login and logout API calls
 * @description_zh 封裝登入與登出 API 呼叫
 */

export const loginApi = async (data: LoginRequest): Promise<LoginResponse> => {
    const response = await axiosInstance.post<ApiResponse<LoginResponse>, ApiResponse<LoginResponse>>('/v1/auth/login', data);
    return response.data;
};

export const logoutApi = async (): Promise<void> => {
    await axiosInstance.post('/v1/auth/logout');
};
