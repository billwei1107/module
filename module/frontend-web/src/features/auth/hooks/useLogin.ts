import { useState } from 'react';
import { isAxiosError } from 'axios';
import { loginApi } from '../api/authApi';
import type { LoginRequest } from '../types';
import { useAuthStore } from '../../../shared/store/authStore';
import { useNavigate } from 'react-router-dom';

/**
 * @file useLogin.ts
 * @description 登入流程 Hook / Login flow hook
 * @description_en Handles login submission, auth state updates, and navigation
 * @description_zh 處理登入送出、認證狀態更新與頁面跳轉
 */

export const useLogin = () => {
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const setAuth = useAuthStore((state) => state.setAuth);
    const navigate = useNavigate();

    const login = async (data: LoginRequest) => {
        setLoading(true);
        setError(null);
        try {
            const response = await loginApi(data);
            setAuth({ id: response.userId, username: response.username }, response.token);
            navigate('/department'); // 跳轉到組織管理做為預設頁
        } catch (err: unknown) {
            const message = isAxiosError<{ message?: string }>(err) ? err.response?.data?.message : undefined;
            setError(message || 'Login failed');
            throw err;
        } finally {
            setLoading(false);
        }
    };

    return { login, loading, error };
};
