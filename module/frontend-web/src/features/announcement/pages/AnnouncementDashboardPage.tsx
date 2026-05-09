/**
 * @file AnnouncementDashboardPage.tsx
 * @description 公告系統驗證頁 / Announcement dashboard verification page
 * @description_en Provides a minimal smoke-test UI for announcement module APIs
 * @description_zh 提供母體庫驗證公告系統 API 的最小頁面
 */

import { useEffect, useMemo, useState } from 'react';
import { Alert, Button, Chip, Paper, Stack, Typography } from '@mui/material';
import { confirmAnnouncement, createAnnouncement, getAnnouncements, getVisibleAnnouncements, markAnnouncementRead, publishDueAnnouncements } from '../api/announcementApi';
import type { Announcement } from '../types';

export const AnnouncementDashboardPage = () => {
    const [announcements, setAnnouncements] = useState<Announcement[]>([]);
    const [visibleAnnouncements, setVisibleAnnouncements] = useState<Announcement[]>([]);
    const [message, setMessage] = useState('');
    const firstAnnouncement = useMemo(() => announcements[0], [announcements]);
    const firstVisible = useMemo(() => visibleAnnouncements[0], [visibleAnnouncements]);

    const loadData = async () => {
        const [all, visible] = await Promise.all([
            getAnnouncements(),
            getVisibleAnnouncements({ userId: 'employee-1', departmentId: 'dept-1', companyId: 'company-1' }),
        ]);
        setAnnouncements(all);
        setVisibleAnnouncements(visible);
    };

    useEffect(() => { loadData().catch(() => setMessage('載入公告資料失敗 / Failed to load announcements')); }, []);

    const handleCreate = async () => {
        try {
            await createAnnouncement({ title: '門市營運公告', content: '今日完成設備盤點', category: '營運', publisherId: 'manager-1', scheduledPublishAt: new Date(Date.now() - 60000).toISOString(), requiresConfirmation: true, targets: [{ targetType: 'DEPARTMENT', targetId: 'dept-1' }] });
            setMessage('公告已建立 / Announcement created');
            await loadData();
        } catch { setMessage('建立公告失敗 / Failed to create announcement'); }
    };

    const handlePublish = async () => {
        try {
            await publishDueAnnouncements();
            setMessage('到期公告已發布 / Due announcements published');
            await loadData();
        } catch { setMessage('發布公告失敗 / Failed to publish announcements'); }
    };

    const handleRead = async () => {
        if (!firstVisible) return;
        try {
            const read = await markAnnouncementRead(firstVisible.id, 'employee-1');
            setVisibleAnnouncements((current) => current.map((item) => item.id === read.id ? read : item));
            setAnnouncements((current) => current.map((item) => item.id === read.id ? read : item));
            setMessage('公告已讀 / Announcement read');
        } catch { setMessage('標記已讀失敗 / Failed to mark announcement read'); }
    };

    const handleConfirm = async () => {
        if (!firstVisible) return;
        try {
            const confirmed = await confirmAnnouncement(firstVisible.id, 'employee-1');
            setVisibleAnnouncements((current) => current.map((item) => item.id === confirmed.id ? confirmed : item));
            setAnnouncements((current) => current.map((item) => item.id === confirmed.id ? confirmed : item));
            setMessage('回條已確認 / Announcement confirmed');
        } catch { setMessage('確認回條失敗 / Failed to confirm announcement'); }
    };

    return (
        <Stack spacing={3}>
            <Typography variant="h5">公告系統</Typography>
            {message && <Alert severity="info">{message}</Alert>}
            <Paper sx={{ p: 3 }}>
                <Typography variant="h6" sx={{ mb: 2 }}>公告流程</Typography>
                <Stack direction={{ xs: 'column', md: 'row' }} spacing={2}>
                    <Button variant="contained" onClick={handleCreate}>建立公告</Button>
                    <Button variant="outlined" onClick={handlePublish} disabled={!firstAnnouncement}>發布到期公告</Button>
                    <Button variant="outlined" onClick={handleRead} disabled={!firstVisible}>標記已讀</Button>
                    <Button variant="outlined" onClick={handleConfirm} disabled={!firstVisible}>確認回條</Button>
                </Stack>
                <Stack direction="row" spacing={1} sx={{ mt: 2, flexWrap: 'wrap' }}>
                    {announcements.map((announcement) => <Chip key={announcement.id} label={`${announcement.title} ${announcement.status}`} />)}
                </Stack>
            </Paper>
            <Paper sx={{ p: 3 }}>
                <Typography variant="h6" sx={{ mb: 2 }}>公告狀態</Typography>
                <Typography>公告數：{announcements.length}</Typography>
                <Typography>可見公告數：{visibleAnnouncements.length}</Typography>
                <Typography>公告狀態：{firstAnnouncement?.status ?? '尚無公告'}</Typography>
                <Typography>已讀狀態：{firstVisible?.read ? '已讀' : '未讀'}</Typography>
                <Typography>回條狀態：{firstVisible?.confirmed ? '已確認' : '未確認'}</Typography>
            </Paper>
        </Stack>
    );
};
