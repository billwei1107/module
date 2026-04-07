import axiosInstance from '@/shared/api/axiosInstance';
import type { ApiResponse } from '@/shared/types';
import type { Company, Department, DepartmentTreeDTO, Employee, Position } from '../types';

export const organizationApi = {
    // Companies
    getCompanies: () => axiosInstance.get<ApiResponse<Company[]>>('/companies').then(res => res.data),
    createCompany: (data: Partial<Company>) => axiosInstance.post<ApiResponse<Company>>('/companies', data).then(res => res.data),

    // Departments
    getDepartmentTree: (companyId: string) => axiosInstance.get<ApiResponse<DepartmentTreeDTO[]>>(`/departments/tree?companyId=${companyId}`).then(res => res.data),
    createDepartment: (data: Partial<Department>) => axiosInstance.post<ApiResponse<Department>>('/departments', data).then(res => res.data),

    // Positions
    getPositions: () => axiosInstance.get<ApiResponse<Position[]>>('/positions').then(res => res.data),
    createPosition: (data: Partial<Position>) => axiosInstance.post<ApiResponse<Position>>('/positions', data).then(res => res.data),

    // Employees
    getEmployees: (params?: { companyId?: string; departmentId?: string }) =>
        axiosInstance.get<ApiResponse<Employee[]>>('/employees', { params }).then(res => res.data),
    createEmployee: (data: Partial<Employee>) => axiosInstance.post<ApiResponse<Employee>>('/employees', data).then(res => res.data),
    resignEmployee: (id: string) => axiosInstance.post<ApiResponse<void>>(`/employees/${id}/resign`).then(res => res.data)
};
