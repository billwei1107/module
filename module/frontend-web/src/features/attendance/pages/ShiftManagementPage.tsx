/**
 * @file ShiftManagementPage.tsx
 * @description 班表管理頁面 / Shift management page
 * @description_en CRUD for shift schedules with employee assignment
 * @description_zh 班別 CRUD 設定與員工指派
 */
import { useEffect, useState } from 'react';
import {
    Box, Typography, Button, Table, TableHead, TableRow, TableCell,
    TableBody, Dialog, DialogTitle, DialogContent, DialogActions,
    TextField, Stack, CircularProgress, MenuItem
} from '@mui/material';
import { getShifts, createShift, updateShift, deleteShift } from '../api/attendanceApi';
import type { ShiftSchedule } from '../types';

const SHIFT_TYPES = ['FIXED', 'FLEXIBLE', 'ROTATING'] as const;

const emptyForm = (): Partial<ShiftSchedule> => ({
    name: '', type: 'FIXED', startTime: '09:00', endTime: '18:00', graceMinutes: 5,
    breakStart: '12:00', breakEnd: '13:00'
});

export const ShiftManagementPage = () => {
    const [shifts, setShifts] = useState<ShiftSchedule[]>([]);
    const [loading, setLoading] = useState(false);
    const [dialogOpen, setDialogOpen] = useState(false);
    const [editTarget, setEditTarget] = useState<ShiftSchedule | null>(null);
    const [form, setForm] = useState<Partial<ShiftSchedule>>(emptyForm());

    const fetchShifts = async () => {
        setLoading(true);
        try { setShifts(await getShifts()); }
        finally { setLoading(false); }
    };

    useEffect(() => { fetchShifts(); }, []);

    const openCreate = () => { setEditTarget(null); setForm(emptyForm()); setDialogOpen(true); };
    const openEdit = (s: ShiftSchedule) => { setEditTarget(s); setForm({ ...s }); setDialogOpen(true); };

    const handleSave = async () => {
        if (editTarget) {
            await updateShift(editTarget.id, form);
        } else {
            await createShift(form);
        }
        setDialogOpen(false);
        fetchShifts();
    };

    const handleDelete = async (id: string) => {
        if (window.confirm('確認刪除此班表？/ Confirm delete this shift?')) {
            await deleteShift(id);
            fetchShifts();
        }
    };

    return (
        <Box sx={{ p: 4, maxWidth: 900, margin: '0 auto' }}>
            <Stack direction="row" justifyContent="space-between" alignItems="center" sx={{ mb: 3 }}>
                <Typography variant="h4" fontWeight="bold">班表管理 / Shift Management</Typography>
                <Button variant="contained" onClick={openCreate}>新增班表 / Add Shift</Button>
            </Stack>

            {loading ? <CircularProgress /> : (
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell>班別名稱 / Name</TableCell>
                            <TableCell>類型 / Type</TableCell>
                            <TableCell>上班時間 / Start</TableCell>
                            <TableCell>下班時間 / End</TableCell>
                            <TableCell>遲到寬限 / Grace</TableCell>
                            <TableCell>操作 / Actions</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {shifts.map(s => (
                            <TableRow key={s.id}>
                                <TableCell>{s.name}</TableCell>
                                <TableCell>{s.type}</TableCell>
                                <TableCell>{s.startTime}</TableCell>
                                <TableCell>{s.endTime}</TableCell>
                                <TableCell>{s.graceMinutes} 分</TableCell>
                                <TableCell>
                                    <Stack direction="row" spacing={1}>
                                        <Button size="small" onClick={() => openEdit(s)}>編輯</Button>
                                        <Button size="small" color="error" onClick={() => handleDelete(s.id)}>刪除</Button>
                                    </Stack>
                                </TableCell>
                            </TableRow>
                        ))}
                        {shifts.length === 0 && (
                            <TableRow>
                                <TableCell colSpan={6} align="center">
                                    <Typography color="text.secondary">尚無班表 / No shifts configured</Typography>
                                </TableCell>
                            </TableRow>
                        )}
                    </TableBody>
                </Table>
            )}

            {/* 新增/編輯對話框 / Create/Edit Dialog */}
            <Dialog open={dialogOpen} onClose={() => setDialogOpen(false)} maxWidth="sm" fullWidth>
                <DialogTitle>{editTarget ? '編輯班表 / Edit Shift' : '新增班表 / Add Shift'}</DialogTitle>
                <DialogContent>
                    <Stack spacing={2} sx={{ mt: 1 }}>
                        <TextField label="班別名稱 / Name" value={form.name ?? ''}
                            onChange={e => setForm(f => ({ ...f, name: e.target.value }))} fullWidth />
                        <TextField select label="類型 / Type" value={form.type ?? 'FIXED'}
                            onChange={e => setForm(f => ({ ...f, type: e.target.value as ShiftSchedule['type'] }))} fullWidth>
                            {SHIFT_TYPES.map(t => <MenuItem key={t} value={t}>{t}</MenuItem>)}
                        </TextField>
                        <Stack direction="row" spacing={2}>
                            <TextField label="上班時間 / Start" type="time" value={form.startTime ?? '09:00'}
                                onChange={e => setForm(f => ({ ...f, startTime: e.target.value }))} fullWidth
                                InputLabelProps={{ shrink: true }} />
                            <TextField label="下班時間 / End" type="time" value={form.endTime ?? '18:00'}
                                onChange={e => setForm(f => ({ ...f, endTime: e.target.value }))} fullWidth
                                InputLabelProps={{ shrink: true }} />
                        </Stack>
                        <TextField label="遲到寬限（分）/ Grace Minutes" type="number" value={form.graceMinutes ?? 5}
                            onChange={e => setForm(f => ({ ...f, graceMinutes: Number(e.target.value) }))} fullWidth />
                        <Stack direction="row" spacing={2}>
                            <TextField label="休息開始 / Break Start" type="time" value={form.breakStart ?? '12:00'}
                                onChange={e => setForm(f => ({ ...f, breakStart: e.target.value }))} fullWidth
                                InputLabelProps={{ shrink: true }} />
                            <TextField label="休息結束 / Break End" type="time" value={form.breakEnd ?? '13:00'}
                                onChange={e => setForm(f => ({ ...f, breakEnd: e.target.value }))} fullWidth
                                InputLabelProps={{ shrink: true }} />
                        </Stack>
                    </Stack>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setDialogOpen(false)}>取消 / Cancel</Button>
                    <Button variant="contained" onClick={handleSave}>儲存 / Save</Button>
                </DialogActions>
            </Dialog>
        </Box>
    );
};
