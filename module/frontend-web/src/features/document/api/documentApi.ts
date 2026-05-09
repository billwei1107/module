/**
 * @file documentApi.ts
 * @description 文件管理 API 請求層 / Document API request layer
 * @description_en Wraps document metadata, version, share, and tag endpoints
 * @description_zh 封裝文件中繼資料、版本、分享與標籤 REST 呼叫
 */

import axiosInstance from '../../../shared/api/axiosInstance';
import type { DocumentAccess, DocumentPermission, DocumentRecord, DocumentShare, DocumentTag, DocumentVersion, Folder } from '../types';

interface ApiEnvelope<T> {
    data: T;
}

const unwrapData = <T>(response: ApiEnvelope<T>): T => {
    if (!response || response.data === undefined) {
        throw new Error('Invalid API response');
    }
    return response.data;
};

export const getFolders = async (): Promise<Folder[]> => {
    const response = await axiosInstance.get('/api/v1/documents/folders');
    return unwrapData<Folder[]>(response as ApiEnvelope<Folder[]>);
};

export const createFolder = async (data: { name: string; ownerId: string }): Promise<Folder> => {
    const response = await axiosInstance.post('/api/v1/documents/folders', data);
    return unwrapData<Folder>(response as ApiEnvelope<Folder>);
};

export const getDocuments = async (): Promise<DocumentRecord[]> => {
    const response = await axiosInstance.get('/api/v1/documents');
    return unwrapData<DocumentRecord[]>(response as ApiEnvelope<DocumentRecord[]>);
};

export const registerDocumentMetadata = async (data: {
    folderId?: string;
    fileName: string;
    filePath: string;
    mimeType: string;
    size: number;
    ownerId: string;
}): Promise<DocumentRecord> => {
    const response = await axiosInstance.post('/api/v1/documents/metadata', data);
    return unwrapData<DocumentRecord>(response as ApiEnvelope<DocumentRecord>);
};

export const registerDocumentVersionMetadata = async (
    documentId: string,
    data: { filePath: string; mimeType: string; size: number; uploadedBy: string },
): Promise<DocumentVersion> => {
    const response = await axiosInstance.post(`/api/v1/documents/${documentId}/versions/metadata`, data);
    return unwrapData<DocumentVersion>(response as ApiEnvelope<DocumentVersion>);
};

export const getDocumentVersions = async (documentId: string): Promise<DocumentVersion[]> => {
    const response = await axiosInstance.get(`/api/v1/documents/${documentId}/versions`);
    return unwrapData<DocumentVersion[]>(response as ApiEnvelope<DocumentVersion[]>);
};

export const shareDocument = async (
    documentId: string,
    data: { sharedWith: string; permission: DocumentPermission; sharedBy: string },
): Promise<DocumentShare> => {
    const response = await axiosInstance.post(`/api/v1/documents/${documentId}/shares`, data);
    return unwrapData<DocumentShare>(response as ApiEnvelope<DocumentShare>);
};

export const assignDocumentTag = async (documentId: string, data: { name: string; color: string }): Promise<DocumentTag> => {
    const response = await axiosInstance.post(`/api/v1/documents/${documentId}/tags`, data);
    return unwrapData<DocumentTag>(response as ApiEnvelope<DocumentTag>);
};

export const checkDocumentAccess = async (documentId: string, userId: string, permission: DocumentPermission): Promise<DocumentAccess> => {
    const response = await axiosInstance.get(`/api/v1/documents/${documentId}/access`, { params: { userId, permission } });
    return unwrapData<DocumentAccess>(response as ApiEnvelope<DocumentAccess>);
};
