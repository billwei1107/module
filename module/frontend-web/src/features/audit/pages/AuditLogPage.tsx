/**
 * @file AuditLogPage.tsx
 * @description 稽核日誌驗證頁 / Audit log verification page
 * @description_en Provides a minimal smoke-test UI for audit search and CSV export
 * @description_zh 提供母體庫驗證稽核日誌查詢與 CSV 匯出的最小頁面
 */

import { useEffect, useState } from 'react';
import {
    Alert,
    Button,
    Paper,
    Stack,
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableRow,
    TextField,
    Typography,
} from '@mui/material';
import { exportAuditLogsCsv, getAuditLogs } from '../api/auditApi';
import type { AuditLog, AuditLogQuery } from '../types';

export const AuditLogPage = () => {
    const [logs, setLogs] = useState<AuditLog[]>([]);
    const [moduleFilter, setModuleFilter] = useState('');
    const [actionFilter, setActionFilter] = useState('');
    const [message, setMessage] = useState('');
    const [csvPreview, setCsvPreview] = useState('');

    const buildQuery = (): AuditLogQuery => ({
        module: moduleFilter || undefined,
        action: actionFilter || undefined,
        size: 20,
    });

    const loadLogs = async () => {
        const result = await getAuditLogs(buildQuery());
        setLogs(result.content);
    };

    useEffect(() => {
        loadLogs().catch(() => setMessage('載入稽核日誌失敗 / Failed to load audit logs'));
    }, []);

    // ========================================
    // 匯出驗證 / Export Verification
    // ========================================
    const handleExport = async () => {
        try {
            const csv = await exportAuditLogsCsv(buildQuery());
            setCsvPreview(csv.split('\n').slice(0, 3).join('\n'));
            setMessage('CSV 已產生 / CSV generated');
        } catch {
            setMessage('CSV 匯出失敗 / Failed to export CSV');
        }
    };

    return (
        <Stack spacing={3}>
            <Typography variant="h5">稽核日誌</Typography>
            {message && <Alert severity="info">{message}</Alert>}

            <Paper sx={{ p: 3 }}>
                <Stack direction={{ xs: 'column', md: 'row' }} spacing={2}>
                    <TextField
                        label="模組"
                        value={moduleFilter}
                        onChange={(event) => setModuleFilter(event.target.value)}
                    />
                    <TextField
                        label="動作"
                        value={actionFilter}
                        onChange={(event) => setActionFilter(event.target.value)}
                    />
                    <Button variant="contained" onClick={() => loadLogs().catch(() => setMessage('查詢失敗 / Search failed'))}>
                        查詢
                    </Button>
                    <Button variant="outlined" onClick={handleExport}>
                        匯出 CSV
                    </Button>
                </Stack>
            </Paper>

            <Paper sx={{ p: 3 }}>
                <Table size="small">
                    <TableHead>
                        <TableRow>
                            <TableCell>時間</TableCell>
                            <TableCell>使用者</TableCell>
                            <TableCell>模組</TableCell>
                            <TableCell>動作</TableCell>
                            <TableCell>資源</TableCell>
                            <TableCell>狀態</TableCell>
                            <TableCell>耗時</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {logs.map((log) => (
                            <TableRow key={log.id}>
                                <TableCell>{log.createdAt ?? '-'}</TableCell>
                                <TableCell>{log.userName ?? log.userId ?? '-'}</TableCell>
                                <TableCell>{log.module}</TableCell>
                                <TableCell>{log.action}</TableCell>
                                <TableCell>{log.resourceType ?? '-'}</TableCell>
                                <TableCell>{log.responseStatus ?? '-'}</TableCell>
                                <TableCell>{log.executionTimeMs ?? 0} ms</TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </Paper>

            {csvPreview && (
                <Paper sx={{ p: 3 }}>
                    <Typography variant="h6" sx={{ mb: 1 }}>CSV 預覽</Typography>
                    <Typography component="pre" sx={{ m: 0, whiteSpace: 'pre-wrap' }}>
                        {csvPreview}
                    </Typography>
                </Paper>
            )}
        </Stack>
    );
};
