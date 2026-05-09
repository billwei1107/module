/**
 * @file LeaveRequestPage.tsx
 * @description 請假申請頁面 / Leave request page
 * @description_zh 提供員工建立請假申請並查看個人請假紀錄
 */

import { useEffect, useMemo, useState } from 'react';
import {
    Alert,
    Box,
    Button,
    MenuItem,
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
import { getLeaveRequests, getLeaveTypes, submitLeaveRequest } from '../api/leaveApi';
import type { LeaveRequest, LeaveType } from '../types';

const DEFAULT_EMPLOYEE_ID = 'emp-001';

export const LeaveRequestPage = () => {
    const [employeeId, setEmployeeId] = useState(DEFAULT_EMPLOYEE_ID);
    const [leaveTypeId, setLeaveTypeId] = useState('');
    const [startTime, setStartTime] = useState('');
    const [endTime, setEndTime] = useState('');
    const [reason, setReason] = useState('');
    const [leaveTypes, setLeaveTypes] = useState<LeaveType[]>([]);
    const [requests, setRequests] = useState<LeaveRequest[]>([]);
    const [message, setMessage] = useState('');

    const typeNameMap = useMemo(
        () => new Map(leaveTypes.map((leaveType) => [leaveType.id, leaveType.name])),
        [leaveTypes],
    );

    const loadData = async () => {
        const [typesResult, requestsResult] = await Promise.all([
            getLeaveTypes(),
            getLeaveRequests(employeeId),
        ]);
        setLeaveTypes(typesResult);
        setRequests(requestsResult);
        if (!leaveTypeId && typesResult.length > 0) {
            setLeaveTypeId(typesResult[0].id);
        }
    };

    useEffect(() => {
        loadData().catch(() => setMessage('載入請假資料失敗 / Failed to load leave data'));
    }, []);

    // ========================================
    // 送出請假 / Submit Leave Request
    // ========================================
    const handleSubmit = async () => {
        try {
            await submitLeaveRequest({ employeeId, leaveTypeId, startTime, endTime, reason });
            setReason('');
            setMessage('請假申請已送出 / Leave request submitted');
            await loadData();
        } catch {
            setMessage('請假申請送出失敗 / Failed to submit leave request');
        }
    };

    return (
        <Stack spacing={3}>
            <Typography variant="h5">請假申請</Typography>
            {message && <Alert severity="info">{message}</Alert>}

            <Paper sx={{ p: 3 }}>
                <Stack spacing={2}>
                    <TextField
                        label="員工 ID"
                        value={employeeId}
                        onChange={(event) => setEmployeeId(event.target.value)}
                    />
                    <TextField
                        select
                        label="假別"
                        value={leaveTypeId}
                        onChange={(event) => setLeaveTypeId(event.target.value)}
                    >
                        {leaveTypes.map((leaveType) => (
                            <MenuItem key={leaveType.id} value={leaveType.id}>
                                {leaveType.name}
                            </MenuItem>
                        ))}
                    </TextField>
                    <Box sx={{ display: 'grid', gridTemplateColumns: { xs: '1fr', md: '1fr 1fr' }, gap: 2 }}>
                        <TextField
                            label="開始時間"
                            type="datetime-local"
                            value={startTime}
                            onChange={(event) => setStartTime(event.target.value)}
                            InputLabelProps={{ shrink: true }}
                        />
                        <TextField
                            label="結束時間"
                            type="datetime-local"
                            value={endTime}
                            onChange={(event) => setEndTime(event.target.value)}
                            InputLabelProps={{ shrink: true }}
                        />
                    </Box>
                    <TextField
                        label="請假原因"
                        value={reason}
                        onChange={(event) => setReason(event.target.value)}
                        multiline
                        minRows={3}
                    />
                    <Button variant="contained" onClick={handleSubmit} disabled={!leaveTypeId || !startTime || !endTime}>
                        送出申請
                    </Button>
                </Stack>
            </Paper>

            <Paper sx={{ p: 2 }}>
                <Typography variant="h6" sx={{ mb: 2 }}>我的請假紀錄</Typography>
                <Table size="small">
                    <TableHead>
                        <TableRow>
                            <TableCell>假別</TableCell>
                            <TableCell>開始</TableCell>
                            <TableCell>結束</TableCell>
                            <TableCell>時數</TableCell>
                            <TableCell>狀態</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {requests.map((request) => (
                            <TableRow key={request.id}>
                                <TableCell>{typeNameMap.get(request.leaveTypeId) ?? request.leaveTypeId}</TableCell>
                                <TableCell>{request.startTime}</TableCell>
                                <TableCell>{request.endTime}</TableCell>
                                <TableCell>{request.hours}</TableCell>
                                <TableCell>{request.status}</TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </Paper>
        </Stack>
    );
};
