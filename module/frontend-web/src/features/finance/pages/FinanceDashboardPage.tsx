/**
 * @file FinanceDashboardPage.tsx
 * @description 財務管理驗證頁 / Finance dashboard verification page
 * @description_en Provides a minimal smoke-test UI for finance module APIs
 * @description_zh 提供母體庫驗證財務管理 API 的最小頁面
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
    createAccount,
    createJournalEntry,
    getAccounts,
    getAgingAnalysis,
    getBudgets,
    getJournalEntries,
} from '../api/financeApi';
import type { Account, AgingBucket, Budget, JournalEntry } from '../types';

export const FinanceDashboardPage = () => {
    const [accounts, setAccounts] = useState<Account[]>([]);
    const [entries, setEntries] = useState<JournalEntry[]>([]);
    const [aging, setAging] = useState<AgingBucket[]>([]);
    const [budgets, setBudgets] = useState<Budget[]>([]);
    const [message, setMessage] = useState('');
    const [accountCode, setAccountCode] = useState('1001');
    const [accountName, setAccountName] = useState('現金');

    const firstTwoAccounts = useMemo(() => accounts.slice(0, 2), [accounts]);

    const loadData = async () => {
        const [accountResult, entryResult, agingResult, budgetResult] = await Promise.all([
            getAccounts(),
            getJournalEntries(),
            getAgingAnalysis(),
            getBudgets(),
        ]);
        setAccounts(accountResult);
        setEntries(entryResult);
        setAging(agingResult);
        setBudgets(budgetResult);
    };

    useEffect(() => {
        loadData().catch(() => setMessage('載入財務資料失敗 / Failed to load finance data'));
    }, []);

    // ========================================
    // 科目與傳票驗證 / Account And Journal Verification
    // ========================================
    const handleCreateAccount = async () => {
        try {
            await createAccount({ code: accountCode, name: accountName, type: 'ASSET', level: 1 });
            setMessage('會計科目已建立 / Account created');
            await loadData();
        } catch {
            setMessage('建立會計科目失敗 / Failed to create account');
        }
    };

    const handleCreateBalancedEntry = async () => {
        if (firstTwoAccounts.length < 2) {
            setMessage('至少需要兩個會計科目 / At least two accounts are required');
            return;
        }
        try {
            await createJournalEntry({
                entryNo: `JE-${Date.now()}`,
                date: new Date().toISOString().slice(0, 10),
                description: '平衡傳票驗證',
                lines: [
                    { accountId: firstTwoAccounts[0].id, debitAmount: 100, creditAmount: 0 },
                    { accountId: firstTwoAccounts[1].id, debitAmount: 0, creditAmount: 100 },
                ],
            });
            setMessage('平衡傳票已建立 / Balanced journal entry created');
            await loadData();
        } catch {
            setMessage('建立傳票失敗 / Failed to create journal entry');
        }
    };

    return (
        <Stack spacing={3}>
            <Typography variant="h5">財務管理</Typography>
            {message && <Alert severity="info">{message}</Alert>}

            <Paper sx={{ p: 3 }}>
                <Typography variant="h6" sx={{ mb: 2 }}>會計科目</Typography>
                <Stack direction={{ xs: 'column', md: 'row' }} spacing={2} sx={{ mb: 2 }}>
                    <TextField label="科目代碼" value={accountCode} onChange={(event) => setAccountCode(event.target.value)} />
                    <TextField label="科目名稱" value={accountName} onChange={(event) => setAccountName(event.target.value)} />
                    <Button variant="contained" onClick={handleCreateAccount}>新增科目</Button>
                </Stack>
                <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1 }}>
                    {accounts.map((account) => (
                        <Chip key={account.id} label={`${account.code} ${account.name} ${account.type}`} />
                    ))}
                </Box>
            </Paper>

            <Paper sx={{ p: 3 }}>
                <Stack direction={{ xs: 'column', md: 'row' }} spacing={2} alignItems="center" sx={{ mb: 2 }}>
                    <Typography variant="h6" sx={{ flexGrow: 1 }}>傳票管理</Typography>
                    <Button variant="outlined" onClick={handleCreateBalancedEntry}>建立平衡傳票</Button>
                </Stack>
                <Table size="small">
                    <TableHead>
                        <TableRow>
                            <TableCell>傳票號</TableCell>
                            <TableCell>狀態</TableCell>
                            <TableCell>借方</TableCell>
                            <TableCell>貸方</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {entries.map((entry) => (
                            <TableRow key={entry.id}>
                                <TableCell>{entry.entryNo}</TableCell>
                                <TableCell>{entry.status}</TableCell>
                                <TableCell>{entry.totalDebit}</TableCell>
                                <TableCell>{entry.totalCredit}</TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </Paper>

            <Paper sx={{ p: 3 }}>
                <Typography variant="h6" sx={{ mb: 2 }}>帳齡分析</Typography>
                <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1 }}>
                    {aging.map((bucket) => (
                        <Chip key={bucket.bucket} label={`${bucket.bucket}: ${bucket.amount}`} color={bucket.amount > 0 ? 'warning' : 'default'} />
                    ))}
                </Box>
            </Paper>

            <Paper sx={{ p: 3 }}>
                <Typography variant="h6" sx={{ mb: 2 }}>預算追蹤</Typography>
                <Stack spacing={1}>
                    {budgets.map((budget) => (
                        <Box key={budget.id}>
                            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, flexWrap: 'wrap' }}>
                                <Typography component="span">
                                    {budget.name} / {budget.fiscalYear} / 使用率 {(budget.usageRate * 100).toFixed(1)}%
                                </Typography>
                                {budget.warning && <Chip size="small" color="warning" label="警示" sx={{ ml: 1 }} />}
                            </Box>
                        </Box>
                    ))}
                </Stack>
            </Paper>
        </Stack>
    );
};
