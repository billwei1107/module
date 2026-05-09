/**
 * @file MeetingDashboardPage.tsx
 * @description 會議管理驗證頁 / Meeting dashboard verification page
 * @description_en Provides a minimal smoke-test UI for meeting module APIs
 * @description_zh 提供母體庫驗證會議管理 API 的最小頁面
 */

import { useEffect, useMemo, useState } from 'react';
import { Alert, Button, Chip, Paper, Stack, Typography } from '@mui/material';
import { completeActionItem, createBooking, createMeeting, createMinute, createRoom, getActionItems, getBookings, getMeetings, getRooms } from '../api/meetingApi';
import type { ActionItem, Booking, Meeting, Room } from '../types';

const isoAt = (hour: number) => {
    const date = new Date();
    date.setHours(hour, 0, 0, 0);
    return date.toISOString();
};

const dueDate = () => {
    const date = new Date();
    date.setDate(date.getDate() + 1);
    return date.toISOString().slice(0, 10);
};

export const MeetingDashboardPage = () => {
    const [rooms, setRooms] = useState<Room[]>([]);
    const [bookings, setBookings] = useState<Booking[]>([]);
    const [meetings, setMeetings] = useState<Meeting[]>([]);
    const [actions, setActions] = useState<ActionItem[]>([]);
    const [message, setMessage] = useState('');
    const firstRoom = useMemo(() => rooms[0], [rooms]);
    const firstBooking = useMemo(() => bookings[0], [bookings]);
    const firstMeeting = useMemo(() => meetings[0], [meetings]);
    const firstAction = useMemo(() => actions[0], [actions]);

    const loadData = async () => {
        const [roomResult, bookingResult, meetingResult] = await Promise.all([getRooms(), getBookings(), getMeetings()]);
        setRooms(roomResult);
        setBookings(bookingResult);
        setMeetings(meetingResult);
        if (meetingResult[0]) {
            setActions(await getActionItems(meetingResult[0].id));
        }
    };

    useEffect(() => { loadData().catch(() => setMessage('載入會議資料失敗 / Failed to load meeting data')); }, []);

    const handleSetup = async () => {
        try {
            await createRoom({ name: 'A01 會議室', location: 'Taipei', capacity: 12, equipment: 'TV, Whiteboard' });
            setMessage('會議室已建立 / Meeting room created');
            await loadData();
        } catch { setMessage('建立會議室失敗 / Failed to create meeting room'); }
    };

    const handleBooking = async () => {
        if (!firstRoom) return;
        try {
            await createBooking({ roomId: firstRoom.id, title: '週會預約', organizerId: 'manager-1', startTime: isoAt(10), endTime: isoAt(11) });
            setMessage('預約已建立 / Booking created');
            await loadData();
        } catch { setMessage('預約失敗 / Failed to create booking'); }
    };

    const handleMeeting = async () => {
        if (!firstBooking) return;
        try {
            await createMeeting({ bookingId: firstBooking.id, subject: '營運週會', organizerId: 'manager-1', agenda: 'KPI review', startTime: firstBooking.startTime, endTime: firstBooking.endTime, attendees: [{ attendeeId: 'employee-1', attendeeName: '王小明', email: 'ming@example.com' }] });
            setMessage('會議已建立 / Meeting created');
            await loadData();
        } catch { setMessage('建立會議失敗 / Failed to create meeting'); }
    };

    const handleMinute = async () => {
        if (!firstMeeting) return;
        try {
            const minute = await createMinute({ meetingId: firstMeeting.id, authorId: 'manager-1', content: '完成營運檢討', decisions: '下週追蹤庫存週轉', actionItems: [{ description: '提交改善方案', ownerId: 'employee-1', dueDate: dueDate() }] });
            setActions(minute.actionItems);
            setMessage('會議紀錄已建立 / Meeting minute created');
        } catch { setMessage('建立會議紀錄失敗 / Failed to create meeting minute'); }
    };

    const handleCompleteAction = async () => {
        if (!firstAction) return;
        try {
            const completed = await completeActionItem(firstAction.id);
            setActions((current) => current.map((item) => item.id === completed.id ? completed : item));
            setMessage('決議追蹤已完成 / Action item completed');
        } catch { setMessage('完成決議追蹤失敗 / Failed to complete action item'); }
    };

    return (
        <Stack spacing={3}>
            <Typography variant="h5">會議管理</Typography>
            {message && <Alert severity="info">{message}</Alert>}
            <Paper sx={{ p: 3 }}>
                <Typography variant="h6" sx={{ mb: 2 }}>會議流程</Typography>
                <Stack direction={{ xs: 'column', md: 'row' }} spacing={2}>
                    <Button variant="contained" onClick={handleSetup}>建立會議室</Button>
                    <Button variant="outlined" onClick={handleBooking} disabled={!firstRoom}>預約會議室</Button>
                    <Button variant="outlined" onClick={handleMeeting} disabled={!firstBooking}>建立會議</Button>
                    <Button variant="outlined" onClick={handleMinute} disabled={!firstMeeting}>建立紀錄</Button>
                    <Button variant="outlined" onClick={handleCompleteAction} disabled={!firstAction}>完成決議</Button>
                </Stack>
                <Stack direction="row" spacing={1} sx={{ mt: 2, flexWrap: 'wrap' }}>
                    {rooms.map((room) => <Chip key={room.id} label={`${room.name} ${room.capacity}人`} />)}
                </Stack>
            </Paper>
            <Paper sx={{ p: 3 }}>
                <Typography variant="h6" sx={{ mb: 2 }}>會議狀態</Typography>
                <Typography>會議室數：{rooms.length}</Typography>
                <Typography>預約數：{bookings.length}</Typography>
                <Typography>會議數：{meetings.length}</Typography>
                <Typography>與會者數：{firstMeeting?.attendees.length ?? 0}</Typography>
                <Typography>決議狀態：{firstAction?.status ?? '尚無決議'}</Typography>
            </Paper>
        </Stack>
    );
};
