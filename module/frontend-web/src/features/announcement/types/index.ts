/**
 * @file index.ts
 * @description 公告系統型別定義 / Announcement type definitions
 * @description_en Defines announcement module API data structures for smoke verification UI
 * @description_zh 定義公告系統模組驗證頁使用的 API 資料結構
 */

export type AnnouncementStatus = 'DRAFT' | 'SCHEDULED' | 'PUBLISHED' | 'ARCHIVED';
export type TargetType = 'ALL_COMPANY' | 'COMPANY' | 'DEPARTMENT' | 'USER';

export interface AnnouncementTarget { id: string; announcementId: string; targetType: TargetType; targetId?: string; }
export interface Announcement { id: string; title: string; content: string; category?: string; attachmentUrl?: string; publisherId?: string; scheduledPublishAt?: string; scheduledUnpublishAt?: string; requiresConfirmation: boolean; status: AnnouncementStatus; targets: AnnouncementTarget[]; read: boolean; confirmed: boolean; readCount: number; confirmationCount: number; }
