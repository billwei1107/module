/**
 * @file projectApi.ts
 * @description 專案任務 API 請求層 / Project API request layer
 * @description_en Wraps project, Kanban, and Gantt endpoints
 * @description_zh 封裝專案、看板與甘特圖 REST 呼叫
 */

import axiosInstance from '../../../shared/api/axiosInstance';
import type { GanttData, KanbanBoard, Project } from '../types';

interface ApiEnvelope<T> {
    data: T;
}

const unwrapData = <T>(response: ApiEnvelope<T>): T => {
    if (!response || response.data === undefined) {
        throw new Error('Invalid API response');
    }
    return response.data;
};

export const getProjects = async (): Promise<Project[]> => {
    const response = await axiosInstance.get('/api/v1/projects');
    return unwrapData<Project[]>(response as ApiEnvelope<Project[]>);
};

export const createProject = async (data: {
    name: string;
    ownerId: string;
    startDate: string;
    endDate: string;
    description: string;
}): Promise<Project> => {
    const response = await axiosInstance.post('/api/v1/projects', data);
    return unwrapData<Project>(response as ApiEnvelope<Project>);
};

export const getKanbanBoard = async (projectId: string): Promise<KanbanBoard> => {
    const response = await axiosInstance.get(`/api/v1/projects/${projectId}/kanban`);
    return unwrapData<KanbanBoard>(response as ApiEnvelope<KanbanBoard>);
};

export const getGanttData = async (projectId: string): Promise<GanttData> => {
    const response = await axiosInstance.get(`/api/v1/projects/${projectId}/gantt`);
    return unwrapData<GanttData>(response as ApiEnvelope<GanttData>);
};
