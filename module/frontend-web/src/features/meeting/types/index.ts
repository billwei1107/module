/**
 * @file index.ts
 * @description 會議管理型別定義 / Meeting type definitions
 * @description_en Defines meeting module API data structures for smoke verification UI
 * @description_zh 定義會議管理模組驗證頁使用的 API 資料結構
 */

export interface Room { id: string; name: string; location?: string; capacity: number; equipment?: string; active: boolean; }
export interface Booking { id: string; roomId: string; title: string; organizerId?: string; startTime: string; endTime: string; status: 'BOOKED' | 'CANCELLED'; }
export interface Attendee { id: string; meetingId: string; attendeeId: string; attendeeName?: string; email?: string; response: 'INVITED' | 'ACCEPTED' | 'DECLINED'; }
export interface Meeting { id: string; bookingId?: string; subject: string; organizerId?: string; agenda?: string; startTime: string; endTime: string; status: 'SCHEDULED' | 'COMPLETED' | 'CANCELLED'; attendees: Attendee[]; }
export interface ActionItem { id: string; meetingId: string; minuteId?: string; description: string; ownerId?: string; dueDate?: string; status: 'OPEN' | 'DONE'; }
export interface Minute { id: string; meetingId: string; authorId?: string; content: string; decisions?: string; actionItems: ActionItem[]; }
