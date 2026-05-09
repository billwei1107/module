/**
 * @file AttendanceReportPage.tsx
 * @description 出勤報表頁面 / Attendance report page
 * @description_en Department/individual attendance statistics with summary cards
 * @description_zh 部門或個人出勤統計，顯示出勤天數、遲到次數、加班時數
 */
import { useCallback, useEffect, useState } from 'react';
import {
    Box, Typography, Card, CardContent, Grid,
    TextField, MenuItem, Stack, Button, CircularProgress
} from '@mui/material';
import { getMonthlyRecords } from '../api/attendanceApi';
import type { AttendanceRecord } from '../types';

const now = new Date();

export const AttendanceReportPage = () => {
    const [year, setYear] = useState(now.getFullYear());
    const [month, setMonth] = useState(now.getMonth() + 1);
    const [records, setRecords] = useState<AttendanceRecord[]>([]);
    const [loading, setLoading] = useState(false);

    // 測試用固定 employeeId，實際應從 Auth context 取得
    const employeeId = 'd290f1ee-6c54-4b01-90e6-d701748f0851';

    const fetchReport = useCallback(async () => {
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

    useEffect(() => { fetchReport(); }, [fetchReport]);

    // ========================================
    // 統計計算 / Statistics calculation
    // ========================================
    const totalDays = records.length;
    const normalDays = records.filter(r => r.status === 'NORMAL').length;
    const lateDays = records.filter(r => r.status === 'LATE').length;
    const earlyLeaveDays = records.filter(r => r.status === 'EARLY_LEAVE').length;
    const absentDays = records.filter(r => r.status === 'ABSENT').length;
    const totalOvertimeMinutes = records.reduce((sum, r) => sum + (r.overtimeMinutes ?? 0), 0);
    const totalOvertimeHours = (totalOvertimeMinutes / 60).toFixed(1);

    const summaryCards = [
        { label: '出勤天數 / Present Days', value: totalDays, color: '#1976d2' },
        { label: '正常出勤 / Normal', value: normalDays, color: '#2e7d32' },
        { label: '遲到次數 / Late', value: lateDays, color: '#ed6c02' },
        { label: '早退次數 / Early Leave', value: earlyLeaveDays, color: '#f57c00' },
        { label: '缺勤次數 / Absent', value: absentDays, color: '#d32f2f' },
        { label: '累計加班 / Overtime (hrs)', value: totalOvertimeHours, color: '#7b1fa2' },
    ];

    return (
        <Box sx={{ p: 4, maxWidth: 900, margin: '0 auto' }}>
            <Typography variant="h4" sx={{ mb: 3, fontWeight: 'bold' }}>
                出勤報表 / Attendance Report
            </Typography>

            {/* 篩選器 / Filters */}
            <Stack direction="row" spacing={2} alignItems="center" sx={{ mb: 4 }}>
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
                <Button variant="outlined" onClick={fetchReport}>重新整理 / Refresh</Button>
            </Stack>

            {loading ? <CircularProgress /> : (
                <Grid container spacing={2}>
                    {summaryCards.map(card => (
                        <Grid size={{ xs: 12, sm: 6, md: 4 }} key={card.label}>
                            <Card variant="outlined">
                                <CardContent sx={{ textAlign: 'center' }}>
                                    <Typography variant="h3" sx={{ color: card.color, fontWeight: 'bold' }}>
                                        {card.value}
                                    </Typography>
                                    <Typography color="text.secondary" variant="body2">
                                        {card.label}
                                    </Typography>
                                </CardContent>
                            </Card>
                        </Grid>
                    ))}
                </Grid>
            )}

            {/* 說明 / Note */}
            <Box sx={{ mt: 4, p: 2, bgcolor: 'action.hover', borderRadius: 1 }}>
                <Typography variant="body2" color="text.secondary">
                    * 此頁面目前顯示個人月報統計。部門統計功能需整合組織模塊 API（departmentId 查詢）。
                    <br />
                    * This page shows personal monthly stats. Department-level reports require organization module integration.
                </Typography>
            </Box>
        </Box>
    );
};
