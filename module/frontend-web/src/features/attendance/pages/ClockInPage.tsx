/**
 * @file ClockInPage.tsx
 * @description 打卡頁面 / Clock in/out page
 * @description_en GPS-based clock in/out with today's attendance status display
 * @description_zh GPS 定位一鍵打卡，顯示今日出勤狀態
 */
import { useEffect, useState } from 'react';
import {
    Box, Typography, Button, Card, CardContent,
    Chip, CircularProgress, Alert, Stack
} from '@mui/material';
import { clockIn, clockOut, getTodayRecord } from '../api/attendanceApi';
import type { AttendanceRecord } from '../types';

// ========================================
// 狀態顏色對應 / Status color mapping
// ========================================
const statusColor = (status: string) => {
    const map: Record<string, 'success' | 'warning' | 'error' | 'default'> = {
        NORMAL: 'success',
        LATE: 'warning',
        EARLY_LEAVE: 'warning',
        ABSENT: 'error',
    };
    return map[status] ?? 'default';
};

const statusLabel = (status: string) => {
    const map: Record<string, string> = {
        NORMAL: '正常 / Normal',
        LATE: '遲到 / Late',
        EARLY_LEAVE: '早退 / Early Leave',
        ABSENT: '缺勤 / Absent',
    };
    return map[status] ?? status;
};

export const ClockInPage = () => {
    const [record, setRecord] = useState<AttendanceRecord | null>(null);
    const [loading, setLoading] = useState(true);
    const [actionLoading, setActionLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [message, setMessage] = useState<string | null>(null);

    // ========================================
    // 載入今日打卡記錄 / Load today's record
    // ========================================
    const loadTodayRecord = async () => {
        try {
            setLoading(true);
            const data = await getTodayRecord();
            setRecord(data);
        } catch {
            setError('載入失敗，請重新整理 / Failed to load record');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => { loadTodayRecord(); }, []);

    // ========================================
    // 取得 GPS 座標並打卡 / Get GPS and clock in
    // ========================================
    const handleClockIn = () => {
        setActionLoading(true);
        setError(null);
        navigator.geolocation.getCurrentPosition(
            async (pos) => {
                try {
                    await clockIn({
                        latitude: pos.coords.latitude,
                        longitude: pos.coords.longitude,
                        method: 'GPS',
                    });
                    setMessage('上班打卡成功 / Clocked in successfully');
                    await loadTodayRecord();
                } catch {
                    setError('打卡失敗，請確認是否在打卡範圍內 / Clock-in failed: outside geofence');
                } finally {
                    setActionLoading(false);
                }
            },
            () => {
                setError('無法取得 GPS 位置 / Unable to get GPS location');
                setActionLoading(false);
            }
        );
    };

    const handleClockOut = async () => {
        setActionLoading(true);
        setError(null);
        try {
            await clockOut();
            setMessage('下班打卡成功 / Clocked out successfully');
            await loadTodayRecord();
        } catch {
            setError('打卡下班失敗 / Clock-out failed');
        } finally {
            setActionLoading(false);
        }
    };

    if (loading) return <Box sx={{ display: 'flex', justifyContent: 'center', mt: 8 }}><CircularProgress /></Box>;

    return (
        <Box sx={{ p: 4, maxWidth: 600, margin: '0 auto' }}>
            <Typography variant="h4" sx={{ mb: 4, fontWeight: 'bold' }}>
                打卡 / Clock In
            </Typography>

            {error && <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError(null)}>{error}</Alert>}
            {message && <Alert severity="success" sx={{ mb: 2 }} onClose={() => setMessage(null)}>{message}</Alert>}

            {/* 今日狀態 / Today Status */}
            <Card variant="outlined" sx={{ mb: 3 }}>
                <CardContent>
                    <Typography variant="h6" sx={{ mb: 2 }}>今日出勤狀態 / Today's Status</Typography>
                    {record ? (
                        <Stack spacing={1}>
                            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                                <Typography color="text.secondary">狀態：</Typography>
                                <Chip label={statusLabel(record.status)} color={statusColor(record.status)} size="small" />
                            </Box>
                            <Typography color="text.secondary">
                                上班打卡 / Clock In：{record.clockInTime ? new Date(record.clockInTime).toLocaleTimeString('zh-TW') : '—'}
                            </Typography>
                            <Typography color="text.secondary">
                                下班打卡 / Clock Out：{record.clockOutTime ? new Date(record.clockOutTime).toLocaleTimeString('zh-TW') : '—'}
                            </Typography>
                            {record.overtimeMinutes > 0 && (
                                <Typography color="text.secondary">
                                    加班 / Overtime：{record.overtimeMinutes} 分鐘
                                </Typography>
                            )}
                        </Stack>
                    ) : (
                        <Typography color="text.secondary">今日尚未打卡 / No record yet</Typography>
                    )}
                </CardContent>
            </Card>

            {/* 打卡按鈕 / Clock buttons */}
            <Stack direction="row" spacing={2}>
                <Button
                    variant="contained"
                    color="primary"
                    size="large"
                    fullWidth
                    disabled={!!record?.clockInTime || actionLoading}
                    onClick={handleClockIn}
                >
                    {actionLoading ? <CircularProgress size={24} /> : '上班打卡 / Clock In'}
                </Button>
                <Button
                    variant="contained"
                    color="secondary"
                    size="large"
                    fullWidth
                    disabled={!record?.clockInTime || !!record?.clockOutTime || actionLoading}
                    onClick={handleClockOut}
                >
                    {actionLoading ? <CircularProgress size={24} /> : '下班打卡 / Clock Out'}
                </Button>
            </Stack>
        </Box>
    );
};
