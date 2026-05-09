/**
 * @file AttendanceRecordsPage.tsx
 * @description 打卡記錄頁面 / Attendance records page
 * @description_en Monthly attendance records with color-coded status indicators
 * @description_zh 月度打卡記錄，以顏色標示正常/遲到/缺勤狀態
 */
import { useCallback, useEffect, useState } from 'react';
import {
    Box, Typography, Table, TableHead, TableRow, TableCell,
    TableBody, Chip, CircularProgress, TextField, MenuItem,
    Stack
} from '@mui/material';
import { getMonthlyRecords } from '../api/attendanceApi';
import type { AttendanceRecord } from '../types';

const STATUS_LABELS: Record<string, string> = {
    NORMAL: '正常',
    LATE: '遲到',
    EARLY_LEAVE: '早退',
    ABSENT: '缺勤',
};

const STATUS_COLORS: Record<string, 'success' | 'warning' | 'error' | 'default'> = {
    NORMAL: 'success',
    LATE: 'warning',
    EARLY_LEAVE: 'warning',
    ABSENT: 'error',
};

const formatTime = (iso: string | null) =>
    iso ? new Date(iso).toLocaleTimeString('zh-TW', { hour: '2-digit', minute: '2-digit' }) : '—';

export const AttendanceRecordsPage = () => {
    const now = new Date();
    const [year, setYear] = useState(now.getFullYear());
    const [month, setMonth] = useState(now.getMonth() + 1);
    const [records, setRecords] = useState<AttendanceRecord[]>([]);
    const [loading, setLoading] = useState(false);

    // 測試用固定 employeeId，實際應從 Auth context 取得
    const employeeId = 'd290f1ee-6c54-4b01-90e6-d701748f0851';

    const fetchRecords = useCallback(async () => {
        setLoading(true);
        try {
            const data = await getMonthlyRecords(employeeId, year, month);
            setRecords(data);
        } catch {
            setRecords([]);
        } finally {
            setLoading(false);
        }
    }, [employeeId, year, month]);

    useEffect(() => { fetchRecords(); }, [fetchRecords]);

    // ========================================
    // 統計摘要 / Summary stats
    // ========================================
    const lateCount = records.filter(r => r.status === 'LATE').length;
    const totalOvertime = records.reduce((sum, r) => sum + (r.overtimeMinutes ?? 0), 0);

    return (
        <Box sx={{ p: 4, maxWidth: 900, margin: '0 auto' }}>
            <Typography variant="h4" sx={{ mb: 3, fontWeight: 'bold' }}>
                打卡記錄 / Attendance Records
            </Typography>

            {/* 篩選器 / Filters */}
            <Stack direction="row" spacing={2} sx={{ mb: 3 }}>
                <TextField
                    select label="年份 / Year" value={year}
                    onChange={e => setYear(Number(e.target.value))} size="small" sx={{ width: 120 }}
                >
                    {[2025, 2026, 2027].map(y => <MenuItem key={y} value={y}>{y}</MenuItem>)}
                </TextField>
                <TextField
                    select label="月份 / Month" value={month}
                    onChange={e => setMonth(Number(e.target.value))} size="small" sx={{ width: 120 }}
                >
                    {Array.from({ length: 12 }, (_, i) => i + 1).map(m => (
                        <MenuItem key={m} value={m}>{m} 月</MenuItem>
                    ))}
                </TextField>
            </Stack>

            {/* 統計摘要 / Stats */}
            <Stack direction="row" spacing={3} sx={{ mb: 3 }}>
                <Typography>出勤天數：<strong>{records.length}</strong></Typography>
                <Typography>遲到次數：<strong>{lateCount}</strong></Typography>
                <Typography>累計加班：<strong>{totalOvertime} 分鐘</strong></Typography>
            </Stack>

            {loading ? <CircularProgress /> : (
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell>日期 / Date</TableCell>
                            <TableCell>上班打卡 / Clock In</TableCell>
                            <TableCell>下班打卡 / Clock Out</TableCell>
                            <TableCell>狀態 / Status</TableCell>
                            <TableCell>加班（分鐘）/ Overtime</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {records.map(r => (
                            <TableRow key={r.id}>
                                <TableCell>{r.date}</TableCell>
                                <TableCell>{formatTime(r.clockInTime)}</TableCell>
                                <TableCell>{formatTime(r.clockOutTime)}</TableCell>
                                <TableCell>
                                    <Chip
                                        label={STATUS_LABELS[r.status] ?? r.status}
                                        color={STATUS_COLORS[r.status] ?? 'default'}
                                        size="small"
                                    />
                                </TableCell>
                                <TableCell>{r.overtimeMinutes > 0 ? `${r.overtimeMinutes} 分` : '—'}</TableCell>
                            </TableRow>
                        ))}
                        {records.length === 0 && (
                            <TableRow>
                                <TableCell colSpan={5} align="center">
                                    <Typography color="text.secondary">本月無打卡記錄 / No records this month</Typography>
                                </TableCell>
                            </TableRow>
                        )}
                    </TableBody>
                </Table>
            )}
        </Box>
    );
};
