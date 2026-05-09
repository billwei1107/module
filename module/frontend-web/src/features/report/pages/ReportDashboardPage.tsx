/**
 * @file ReportDashboardPage.tsx
 * @description 報表分析驗證頁 / Report dashboard verification page
 * @description_en Provides a minimal smoke-test UI for report analytics APIs
 * @description_zh 提供母體庫驗證報表分析 API 的最小頁面
 */

import { useEffect, useMemo, useState } from 'react';
import {
    Alert,
    Box,
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
    createDashboard,
    createReportDefinition,
    createReportSchedule,
    createWidget,
    executeReport,
    getBusinessSummary,
    getDashboards,
    getReportDefinitions,
} from '../api/reportApi';
import type { BusinessSummary, Dashboard, ReportDefinition, ReportExecutionResult } from '../types';

export const ReportDashboardPage = () => {
    const [definitions, setDefinitions] = useState<ReportDefinition[]>([]);
    const [dashboards, setDashboards] = useState<Dashboard[]>([]);
    const [summary, setSummary] = useState<BusinessSummary | null>(null);
    const [executionResult, setExecutionResult] = useState<ReportExecutionResult | null>(null);
    const [reportName, setReportName] = useState('應收帳款總覽');
    const [message, setMessage] = useState('');

    const firstDefinition = useMemo(() => definitions[0], [definitions]);
    const firstDashboard = useMemo(() => dashboards[0], [dashboards]);

    const loadData = async () => {
        const [definitionResult, dashboardResult, summaryResult] = await Promise.all([
            getReportDefinitions(),
            getDashboards(),
            getBusinessSummary(),
        ]);
        setDefinitions(definitionResult);
        setDashboards(dashboardResult);
        setSummary(summaryResult);
    };

    useEffect(() => {
        loadData().catch(() => setMessage('載入報表資料失敗 / Failed to load report data'));
    }, []);

    const handleCreateDefinition = async () => {
        try {
            await createReportDefinition({
                name: reportName,
                dataSourceSql: 'select invoice_no, amount, status from fin_invoices',
                columnsJson: '["invoice_no","amount","status"]',
                filtersJson: '{}',
            });
            setMessage('報表定義已建立 / Report definition created');
            await loadData();
        } catch {
            setMessage('建立報表定義失敗 / Failed to create report definition');
        }
    };

    const handleExecuteReport = async () => {
        if (!firstDefinition) {
            return;
        }
        try {
            const result = await executeReport(firstDefinition.id);
            setExecutionResult(result);
            setMessage('報表已執行 / Report executed');
        } catch {
            setMessage('執行報表失敗 / Failed to execute report');
        }
    };

    const handleCreateDashboard = async () => {
        try {
            await createDashboard({ name: '管理儀表板', ownerId: 'emp-001', layoutJson: '{}' });
            setMessage('儀表板已建立 / Dashboard created');
            await loadData();
        } catch {
            setMessage('建立儀表板失敗 / Failed to create dashboard');
        }
    };

    const handleCreateWidgetAndSchedule = async () => {
        if (!firstDashboard || !firstDefinition) {
            return;
        }
        try {
            await Promise.all([
                createWidget(firstDashboard.id, {
                    title: '應收總額',
                    type: 'NUMBER',
                    dataSourceSql: 'select sum(amount) as amount from fin_invoices',
                    positionJson: '{}',
                }),
                createReportSchedule({
                    definitionId: firstDefinition.id,
                    cronExpression: '0 0 9 * * *',
                    recipientEmails: 'ops@example.com',
                }),
            ]);
            setMessage('元件與排程已建立 / Widget and schedule created');
            await loadData();
        } catch {
            setMessage('建立元件或排程失敗 / Failed to create widget or schedule');
        }
    };

    return (
        <Stack spacing={3}>
            <Typography variant="h5">報表分析</Typography>
            {message && <Alert severity="info">{message}</Alert>}

            <Paper sx={{ p: 3 }}>
                <Typography variant="h6" sx={{ mb: 2 }}>跨模組統計</Typography>
                <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1 }}>
                    <Chip label={`出勤筆數 ${summary?.attendanceRecords ?? 0}`} />
                    <Chip label={`加班分鐘 ${summary?.overtimeMinutes ?? 0}`} />
                    <Chip label={`未收付金額 ${summary?.openInvoiceAmount ?? 0}`} />
                    <Chip label={`薪資實發 ${summary?.payrollNetPay ?? 0}`} />
                </Box>
            </Paper>

            <Paper sx={{ p: 3 }}>
                <Typography variant="h6" sx={{ mb: 2 }}>報表定義</Typography>
                <Stack direction={{ xs: 'column', md: 'row' }} spacing={2} sx={{ mb: 2 }}>
                    <TextField label="報表名稱" value={reportName} onChange={(event) => setReportName(event.target.value)} />
                    <Button variant="contained" onClick={handleCreateDefinition}>新增定義</Button>
                    <Button variant="outlined" onClick={handleExecuteReport} disabled={!firstDefinition}>執行報表</Button>
                </Stack>
                <Stack direction="row" spacing={1} sx={{ flexWrap: 'wrap' }}>
                    {definitions.map((definition) => (
                        <Chip key={definition.id} label={definition.name} color={definition.active ? 'success' : 'default'} />
                    ))}
                </Stack>
            </Paper>

            <Paper sx={{ p: 3 }}>
                <Typography variant="h6" sx={{ mb: 2 }}>執行結果</Typography>
                <Typography sx={{ mb: 2 }}>筆數：{executionResult?.rowCount ?? 0}</Typography>
                <Table size="small">
                    <TableHead>
                        <TableRow>
                            {(executionResult?.columns ?? []).map((column) => <TableCell key={column}>{column}</TableCell>)}
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {(executionResult?.rows ?? []).map((row, index) => (
                            <TableRow key={`${executionResult?.definitionId}-${index}`}>
                                {executionResult?.columns.map((column) => <TableCell key={column}>{String(row[column] ?? '')}</TableCell>)}
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </Paper>

            <Paper sx={{ p: 3 }}>
                <Typography variant="h6" sx={{ mb: 2 }}>儀表板</Typography>
                <Stack direction={{ xs: 'column', md: 'row' }} spacing={2} sx={{ mb: 2 }}>
                    <Button variant="contained" onClick={handleCreateDashboard}>新增儀表板</Button>
                    <Button variant="outlined" onClick={handleCreateWidgetAndSchedule} disabled={!firstDashboard || !firstDefinition}>
                        新增元件與排程
                    </Button>
                </Stack>
                <Stack spacing={1}>
                    {dashboards.map((dashboard) => (
                        <Typography key={dashboard.id}>
                            {dashboard.name} / 元件數 {dashboard.widgets.length}
                        </Typography>
                    ))}
                </Stack>
            </Paper>
        </Stack>
    );
};
