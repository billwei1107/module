/**
 * @file LeaveApprovalPage.tsx
 * @description 請假審核頁面 / Leave approval page
 * @description_zh 提供主管查看待審核請假申請並執行通過或駁回
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
    Typography,
} from '@mui/material';
import { approveLeaveRequest, getPendingLeaveRequests, rejectLeaveRequest } from '../api/leaveApi';
import type { LeaveRequest } from '../types';

export const LeaveApprovalPage = () => {
    const [requests, setRequests] = useState<LeaveRequest[]>([]);
    const [message, setMessage] = useState('');

    const loadPendingRequests = async () => {
        setRequests(await getPendingLeaveRequests());
    };

    useEffect(() => {
        loadPendingRequests().catch(() => setMessage('載入待審核清單失敗 / Failed to load pending requests'));
    }, []);

    // ========================================
    // 審核動作 / Review Actions
    // ========================================
    const handleReview = async (id: string, action: 'approve' | 'reject') => {
        try {
            if (action === 'approve') {
                await approveLeaveRequest(id);
            } else {
                await rejectLeaveRequest(id);
            }
            setMessage('審核完成 / Review completed');
            await loadPendingRequests();
        } catch {
            setMessage('審核失敗 / Review failed');
        }
    };

    return (
        <Stack spacing={3}>
            <Typography variant="h5">請假審核</Typography>
            {message && <Alert severity="info">{message}</Alert>}

            <Paper sx={{ p: 2 }}>
                <Table size="small">
                    <TableHead>
                        <TableRow>
                            <TableCell>員工</TableCell>
                            <TableCell>假別 ID</TableCell>
                            <TableCell>開始</TableCell>
                            <TableCell>結束</TableCell>
                            <TableCell>時數</TableCell>
                            <TableCell>原因</TableCell>
                            <TableCell align="right">操作</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {requests.map((request) => (
                            <TableRow key={request.id}>
                                <TableCell>{request.employeeId}</TableCell>
                                <TableCell>{request.leaveTypeId}</TableCell>
                                <TableCell>{request.startTime}</TableCell>
                                <TableCell>{request.endTime}</TableCell>
                                <TableCell>{request.hours}</TableCell>
                                <TableCell>{request.reason}</TableCell>
                                <TableCell align="right">
                                    <Button size="small" onClick={() => handleReview(request.id, 'approve')}>
                                        通過
                                    </Button>
                                    <Button size="small" color="error" onClick={() => handleReview(request.id, 'reject')}>
                                        駁回
                                    </Button>
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </Paper>
        </Stack>
    );
};
