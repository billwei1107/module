/**
 * @file index.ts
 * @description 文件管理型別定義 / Document type definitions
 * @description_en Defines document module API data structures for smoke verification UI
 * @description_zh 定義文件管理模組驗證頁使用的 API 資料結構
 */

export type DocumentPermission = 'READ' | 'EDIT' | 'SHARE';

export interface Folder {
    id: string;
    parentId?: string;
    name: string;
    path: string;
    ownerId?: string;
}

export interface DocumentTag {
    id: string;
    documentId: string;
    name: string;
    color: string;
}

export interface DocumentRecord {
    id: string;
    folderId?: string;
    fileName: string;
    filePath: string;
    mimeType: string;
    size: number;
    version: number;
    ownerId?: string;
    tags: DocumentTag[];
}

export interface DocumentVersion {
    id: string;
    documentId: string;
    version: number;
    filePath: string;
    mimeType: string;
    size: number;
    uploadedBy?: string;
}

export interface DocumentShare {
    id: string;
    documentId: string;
    sharedWith: string;
    permission: DocumentPermission;
    sharedBy?: string;
    expiresAt?: string;
}

export interface DocumentAccess {
    documentId: string;
    userId: string;
    requiredPermission: DocumentPermission;
    allowed: boolean;
}
