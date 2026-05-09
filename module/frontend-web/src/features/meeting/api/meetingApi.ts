/**
 * @file meetingApi.ts
 * @description 會議管理 API 請求層 / Meeting API request layer
 * @description_en Wraps meeting room, booking, meeting, minute, and action item endpoints
 * @description_zh 封裝會議室、預約、會議、紀錄與決議追蹤 REST 呼叫
 */

import axiosInstance from '../../../shared/api/axiosInstance';
import type { ActionItem, Booking, Meeting, Minute, Room } from '../types';

interface ApiEnvelope<T> { data: T; }
const unwrapData = <T>(response: ApiEnvelope<T>): T => {
    if (!response || response.data === undefined) throw new Error('Invalid API response');
    return response.data;
};

export const createRoom = async (data: { name: string; location: string; capacity: number; equipment: string }): Promise<Room> => unwrapData(await axiosInstance.post('/api/v1/meetings/rooms', data) as ApiEnvelope<Room>);
export const getRooms = async (): Promise<Room[]> => unwrapData(await axiosInstance.get('/api/v1/meetings/rooms') as ApiEnvelope<Room[]>);
export const createBooking = async (data: { roomId: string; title: string; organizerId: string; startTime: string; endTime: string }): Promise<Booking> => unwrapData(await axiosInstance.post('/api/v1/meetings/bookings', data) as ApiEnvelope<Booking>);
export const getBookings = async (): Promise<Booking[]> => unwrapData(await axiosInstance.get('/api/v1/meetings/bookings') as ApiEnvelope<Booking[]>);
export const createMeeting = async (data: { bookingId?: string; subject: string; organizerId: string; agenda: string; startTime: string; endTime: string; attendees: Array<{ attendeeId: string; attendeeName: string; email: string }> }): Promise<Meeting> => unwrapData(await axiosInstance.post('/api/v1/meetings', data) as ApiEnvelope<Meeting>);
export const getMeetings = async (): Promise<Meeting[]> => unwrapData(await axiosInstance.get('/api/v1/meetings') as ApiEnvelope<Meeting[]>);
export const createMinute = async (data: { meetingId: string; authorId: string; content: string; decisions: string; actionItems: Array<{ description: string; ownerId: string; dueDate: string }> }): Promise<Minute> => unwrapData(await axiosInstance.post('/api/v1/meetings/minutes', data) as ApiEnvelope<Minute>);
export const completeActionItem = async (id: string): Promise<ActionItem> => unwrapData(await axiosInstance.post(`/api/v1/meetings/action-items/${id}/complete`) as ApiEnvelope<ActionItem>);
export const getActionItems = async (meetingId: string): Promise<ActionItem[]> => unwrapData(await axiosInstance.get(`/api/v1/meetings/${meetingId}/action-items`) as ApiEnvelope<ActionItem[]>);
