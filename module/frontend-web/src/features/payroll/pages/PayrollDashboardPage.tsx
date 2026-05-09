/**
 * @file PayrollDashboardPage.tsx
 * @description 薪資管理驗證頁 / Payroll dashboard verification page
 * @description_en Provides a minimal smoke-test UI for payroll module APIs
 * @description_zh 提供母體庫驗證薪資管理 API 的最小頁面
 */

import { useEffect, useState } from 'react';
import {
    Alert,
    Button,
    Chip,
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
import {
    calculatePayroll,
    getPayrollRecords,
    getSalaryStructures,
    upsertSalaryStructure,
} from '../api/payrollApi';
import type { PayrollRecord, SalaryStructure } from '../types';

export const PayrollDashboardPage = () => {
    const [structures, setStructures] = useState<SalaryStructure[]>([]);
    const [records, setRecords] = useState<PayrollRecord[]>([]);
    const [employeeId, setEmployeeId] = useState('emp-001');
    const [month, setMonth] = useState('2026-05');
    const [baseSalary, setBaseSalary] = useState(60000);
    const [message, setMessage] = useState('');

    const loadData = async () => {
        const [structureResult, recordResult] = await Promise.all([
            getSalaryStructures(),
            getPayrollRecords(),
        ]);
        setStructures(structureResult);
        setRecords(recordResult);
    };

    useEffect(() => {
        loadData().catch(() => setMessage('載入薪資資料失敗 / Failed to load payroll data'));
    }, []);

    const handleSaveStructure = async () => {
        try {
            await upsertSalaryStructure({
                name: '一般月薪',
                employeeId,
                type: 'MONTHLY',
                baseSalary,
                hourlyRate: 0,
            });
            setMessage('薪資結構已儲存 / Salary structure saved');
            await loadData();
        } catch {
            setMessage('薪資結構儲存失敗 / Failed to save salary structure');
        }
    };

    const handleCalculate = async () => {
        try {
            await calculatePayroll(employeeId, month);
            setMessage('薪資已計算 / Payroll calculated');
            await loadData();
        } catch {
            setMessage('薪資計算失敗 / Failed to calculate payroll');
        }
    };

    return (
        <Stack spacing={3}>
            <Typography variant="h5">薪資管理</Typography>
            {message && <Alert severity="info">{message}</Alert>}

            <Paper sx={{ p: 3 }}>
                <Typography variant="h6" sx={{ mb: 2 }}>薪資結構</Typography>
                <Stack direction={{ xs: 'column', md: 'row' }} spacing={2}>
                    <TextField label="員工 ID" value={employeeId} onChange={(event) => setEmployeeId(event.target.value)} />
                    <TextField label="月份" value={month} onChange={(event) => setMonth(event.target.value)} />
                    <TextField
                        label="底薪"
                        type="number"
                        value={baseSalary}
                        onChange={(event) => setBaseSalary(Number(event.target.value))}
                    />
                    <Button variant="contained" onClick={handleSaveStructure}>儲存結構</Button>
                    <Button variant="outlined" onClick={handleCalculate}>計算月薪</Button>
                </Stack>
                <Stack direction="row" spacing={1} sx={{ mt: 2, flexWrap: 'wrap' }}>
                    {structures.map((structure) => (
                        <Chip
                            key={structure.id}
                            label={`${structure.employeeId}: ${structure.baseSalary}`}
                            color={structure.active ? 'success' : 'default'}
                        />
                    ))}
                </Stack>
            </Paper>

            <Paper sx={{ p: 3 }}>
                <Typography variant="h6" sx={{ mb: 2 }}>薪資紀錄</Typography>
                <Table size="small">
                    <TableHead>
                        <TableRow>
                            <TableCell>員工</TableCell>
                            <TableCell>月份</TableCell>
                            <TableCell>給付</TableCell>
                            <TableCell>扣款</TableCell>
                            <TableCell>實發</TableCell>
                            <TableCell>狀態</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {records.map((record) => (
                            <TableRow key={record.id}>
                                <TableCell>{record.employeeId}</TableCell>
                                <TableCell>{record.yearMonth}</TableCell>
                                <TableCell>{record.totalEarnings}</TableCell>
                                <TableCell>{record.totalDeductions}</TableCell>
                                <TableCell>{record.netPay}</TableCell>
                                <TableCell>{record.status}</TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </Paper>
        </Stack>
    );
};
