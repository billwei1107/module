/**
 * @file announcementApi.ts
 * @description 公告系統 API 請求層 / Announcement API request layer
 * @description_en Wraps announcement create, schedule, visibility, read, and confirmation endpoints
 * @description_zh 封裝公告建立、排程、可見範圍、已讀與回條確認 REST 呼叫
 */

import axiosInstance from '../../../shared/api/axiosInstance';
import type { Announcement, TargetType } from '../types';

interface ApiEnvelope<T> { data: T; }
const unwrapData = <T>(response: ApiEnvelope<T>): T => {
    if (!response || response.data === undefined) throw new Error('Invalid API response');
    return response.data;
};

export const createAnnouncement = async (data: { title: string; content: string; category: string; publisherId: string; scheduledPublishAt: string; scheduledUnpublishAt?: string; requiresConfirmation: boolean; targets: Array<{ targetType: TargetType; targetId?: string }> }): Promise<Announcement> => unwrapData(await axiosInstance.post('/api/v1/announcements', data) as ApiEnvelope<Announcement>);
export const getAnnouncements = async (): Promise<Announcement[]> => unwrapData(await axiosInstance.get('/api/v1/announcements') as ApiEnvelope<Announcement[]>);
export const publishDueAnnouncements = async (): Promise<number> => unwrapData(await axiosInstance.post('/api/v1/announcements/publish-due') as ApiEnvelope<number>);
export const archiveExpiredAnnouncements = async (): Promise<number> => unwrapData(await axiosInstance.post('/api/v1/announcements/archive-expired') as ApiEnvelope<number>);
export const getVisibleAnnouncements = async (data: { userId: string; companyId?: string; departmentId?: string }): Promise<Announcement[]> => unwrapData(await axiosInstance.post('/api/v1/announcements/visible', data) as ApiEnvelope<Announcement[]>);
export const markAnnouncementRead = async (id: string, userId: string): Promise<Announcement> => unwrapData(await axiosInstance.post(`/api/v1/announcements/${id}/read`, null, { params: { userId } }) as ApiEnvelope<Announcement>);
export const confirmAnnouncement = async (id: string, userId: string): Promise<Announcement> => unwrapData(await axiosInstance.post(`/api/v1/announcements/${id}/confirm`, null, { params: { userId } }) as ApiEnvelope<Announcement>);
