import { useState } from 'react';
import { loginApi } from '../api/authApi';
import type { LoginRequest } from '../types';
import { useAuthStore } from '../../../shared/store/authStore';
import { useNavigate } from 'react-router-dom';

export const useLogin = () => {
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const setAuth = useAuthStore((state: any) => state.setAuth);
    const navigate = useNavigate();

    const login = async (data: LoginRequest) => {
        setLoading(true);
        setError(null);
        try {
            const response = await loginApi(data);
            setAuth(response.token, response.userId, response.username);
            navigate('/');
        } catch (err: any) {
            setError(err.response?.data?.message || 'Login failed');
            throw err;
        } finally {
            setLoading(false);
        }
    };

    return { login, loading, error };
};
