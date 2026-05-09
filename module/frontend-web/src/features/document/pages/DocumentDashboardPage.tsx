/**
 * @file DocumentDashboardPage.tsx
 * @description 文件管理驗證頁 / Document dashboard verification page
 * @description_en Provides a minimal smoke-test UI for document metadata APIs
 * @description_zh 提供母體庫驗證文件中繼資料 API 的最小頁面
 */

import { useEffect, useMemo, useState } from 'react';
import {
    Alert,
    Box,
    Button,
    Chip,
    MenuItem,
    Paper,
    Stack,
    TextField,
    Typography,
} from '@mui/material';
import {
    assignDocumentTag,
    checkDocumentAccess,
    createFolder,
    getDocumentVersions,
    getDocuments,
    getFolders,
    registerDocumentMetadata,
    registerDocumentVersionMetadata,
    shareDocument,
} from '../api/documentApi';
import type { DocumentAccess, DocumentPermission, DocumentRecord, DocumentVersion, Folder } from '../types';

export const DocumentDashboardPage = () => {
    const [folders, setFolders] = useState<Folder[]>([]);
    const [documents, setDocuments] = useState<DocumentRecord[]>([]);
    const [versions, setVersions] = useState<DocumentVersion[]>([]);
    const [access, setAccess] = useState<DocumentAccess | null>(null);
    const [folderName, setFolderName] = useState('營運文件');
    const [fileName, setFileName] = useState('store-policy.pdf');
    const [permission, setPermission] = useState<DocumentPermission>('READ');
    const [message, setMessage] = useState('');

    const firstDocument = useMemo(() => documents[0], [documents]);

    const loadData = async () => {
        const [folderResult, documentResult] = await Promise.all([getFolders(), getDocuments()]);
        setFolders(folderResult);
        setDocuments(documentResult);
        if (documentResult[0]) {
            const versionResult = await getDocumentVersions(documentResult[0].id);
            setVersions(versionResult);
        }
    };

    useEffect(() => {
        loadData().catch(() => setMessage('載入文件資料失敗 / Failed to load document data'));
    }, []);

    const handleCreateFolder = async () => {
        try {
            await createFolder({ name: folderName, ownerId: 'emp-001' });
            setMessage('資料夾已建立 / Folder created');
            await loadData();
        } catch {
            setMessage('建立資料夾失敗 / Failed to create folder');
        }
    };

    const handleRegisterDocument = async () => {
        try {
            await registerDocumentMetadata({
                folderId: folders[0]?.id,
                fileName,
                filePath: `documents/${Date.now()}-${fileName}`,
                mimeType: 'application/pdf',
                size: 2048,
                ownerId: 'emp-001',
            });
            setMessage('文件中繼資料已登錄 / Document metadata registered');
            await loadData();
        } catch {
            setMessage('登錄文件失敗 / Failed to register document');
        }
    };

    const handleRegisterVersion = async () => {
        if (!firstDocument) {
            return;
        }
        try {
            await registerDocumentVersionMetadata(firstDocument.id, {
                filePath: `documents/${Date.now()}-${firstDocument.fileName}`,
                mimeType: firstDocument.mimeType,
                size: firstDocument.size + 512,
                uploadedBy: 'emp-002',
            });
            setMessage('新版中繼資料已登錄 / Version metadata registered');
            await loadData();
        } catch {
            setMessage('登錄新版失敗 / Failed to register version');
        }
    };

    const handleShareAndTag = async () => {
        if (!firstDocument) {
            return;
        }
        try {
            await Promise.all([
                shareDocument(firstDocument.id, { sharedWith: 'emp-002', permission, sharedBy: 'emp-001' }),
                assignDocumentTag(firstDocument.id, { name: 'policy', color: '#2563eb' }),
            ]);
            const accessResult = await checkDocumentAccess(firstDocument.id, 'emp-002', permission);
            setAccess(accessResult);
            setMessage('分享與標籤已更新 / Share and tag updated');
            await loadData();
        } catch {
            setMessage('更新分享或標籤失敗 / Failed to update share or tag');
        }
    };

    return (
        <Stack spacing={3}>
            <Typography variant="h5">文件管理</Typography>
            {message && <Alert severity="info">{message}</Alert>}

            <Paper sx={{ p: 3 }}>
                <Typography variant="h6" sx={{ mb: 2 }}>資料夾</Typography>
                <Stack direction={{ xs: 'column', md: 'row' }} spacing={2} sx={{ mb: 2 }}>
                    <TextField label="資料夾名稱" value={folderName} onChange={(event) => setFolderName(event.target.value)} />
                    <Button variant="contained" onClick={handleCreateFolder}>新增資料夾</Button>
                </Stack>
                <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1 }}>
                    {folders.map((folder) => (
                        <Chip key={folder.id} label={folder.path} />
                    ))}
                </Box>
            </Paper>

            <Paper sx={{ p: 3 }}>
                <Typography variant="h6" sx={{ mb: 2 }}>文件中繼資料</Typography>
                <Stack direction={{ xs: 'column', md: 'row' }} spacing={2} sx={{ mb: 2 }}>
                    <TextField label="文件名稱" value={fileName} onChange={(event) => setFileName(event.target.value)} />
                    <Button variant="contained" onClick={handleRegisterDocument}>登錄文件</Button>
                    <Button variant="outlined" onClick={handleRegisterVersion} disabled={!firstDocument}>登錄新版</Button>
                </Stack>
                <Stack spacing={1}>
                    {documents.map((document) => (
                        <Box key={document.id} sx={{ display: 'flex', flexWrap: 'wrap', gap: 1, alignItems: 'center' }}>
                            <Typography sx={{ minWidth: 220 }}>{document.fileName}</Typography>
                            <Chip size="small" label={`v${document.version}`} />
                            <Chip size="small" label={document.mimeType} />
                            {document.tags.map((tag) => <Chip key={tag.id} size="small" label={tag.name} />)}
                        </Box>
                    ))}
                </Stack>
            </Paper>

            <Paper sx={{ p: 3 }}>
                <Typography variant="h6" sx={{ mb: 2 }}>分享與版本</Typography>
                <Stack direction={{ xs: 'column', md: 'row' }} spacing={2} sx={{ mb: 2 }}>
                    <TextField select label="權限" value={permission} onChange={(event) => setPermission(event.target.value as DocumentPermission)}>
                        <MenuItem value="READ">READ</MenuItem>
                        <MenuItem value="EDIT">EDIT</MenuItem>
                        <MenuItem value="SHARE">SHARE</MenuItem>
                    </TextField>
                    <Button variant="contained" onClick={handleShareAndTag} disabled={!firstDocument}>分享並標籤</Button>
                </Stack>
                <Typography>版本數：{versions.length}</Typography>
                <Typography>emp-002 權限：{access ? String(access.allowed) : '尚未檢查'}</Typography>
            </Paper>
        </Stack>
    );
};
