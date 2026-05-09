/**
 * @file CrmDashboardPage.tsx
 * @description 客戶管理驗證頁 / CRM dashboard verification page
 * @description_en Provides a minimal smoke-test UI for CRM module APIs
 * @description_zh 提供母體庫驗證客戶管理 API 的最小頁面
 */

import { useEffect, useMemo, useState } from 'react';
import { Alert, Box, Button, Chip, Paper, Stack, TextField, Typography } from '@mui/material';
import { createContact, createContract, createCustomer, createInteractionLog, createOpportunity, createQuotation, getCustomers, getExpiringContracts, getFunnelStats, getOpportunities, getPendingFollowUps, getQuotations } from '../api/crmApi';
import type { Contract, Customer, FunnelStats, InteractionLog, Opportunity, Quotation } from '../types';

export const CrmDashboardPage = () => {
    const [customers, setCustomers] = useState<Customer[]>([]);
    const [opportunities, setOpportunities] = useState<Opportunity[]>([]);
    const [quotations, setQuotations] = useState<Quotation[]>([]);
    const [contracts, setContracts] = useState<Contract[]>([]);
    const [followUps, setFollowUps] = useState<InteractionLog[]>([]);
    const [funnel, setFunnel] = useState<FunnelStats | null>(null);
    const [customerName, setCustomerName] = useState('BillW 客戶');
    const [message, setMessage] = useState('');
    const firstCustomer = useMemo(() => customers[0], [customers]);

    const loadData = async () => {
        const [customerResult, opportunityResult, quotationResult, contractResult, followUpResult, funnelResult] = await Promise.all([
            getCustomers(), getOpportunities(), getQuotations(), getExpiringContracts(), getPendingFollowUps(), getFunnelStats(),
        ]);
        setCustomers(customerResult);
        setOpportunities(opportunityResult);
        setQuotations(quotationResult);
        setContracts(contractResult);
        setFollowUps(followUpResult);
        setFunnel(funnelResult);
    };

    useEffect(() => { loadData().catch(() => setMessage('載入客戶資料失敗 / Failed to load CRM data')); }, []);

    const handleCreateCustomer = async () => {
        try {
            await createCustomer({ name: customerName, type: 'COMPANY', grade: 'VIP', ownerId: 'emp-001', phone: '02-12345678', email: 'ops@example.com', address: 'Taipei' });
            setMessage('客戶已建立 / Customer created');
            await loadData();
        } catch { setMessage('建立客戶失敗 / Failed to create customer'); }
    };

    const handleCreateSalesData = async () => {
        if (!firstCustomer) return;
        try {
            await Promise.all([
                createContact({ customerId: firstCustomer.id, name: '王小明', title: '採購', phone: '0912345678', email: 'buyer@example.com', primaryContact: true }),
                createOpportunity({ customerId: firstCustomer.id, name: 'ERP 導入案', stage: 'PROPOSAL', amount: 200000, expectedCloseDate: '2026-06-30', ownerId: 'emp-001' }),
                createQuotation({ quotationNo: `QT-${Date.now()}`, customerId: firstCustomer.id, quoteDate: '2026-05-09', taxInclusive: false, taxRate: 0.05, items: [{ itemName: '導入服務', quantity: 2, unitPrice: 100000 }] }),
                createContract({ contractNo: `CT-${Date.now()}`, customerId: firstCustomer.id, title: 'ERP 維護合約', startDate: '2026-05-01', endDate: '2026-05-31', amount: 30000, status: 'ACTIVE' }),
                createInteractionLog({ customerId: firstCustomer.id, type: 'FOLLOW_UP', handledBy: 'emp-001', note: '追蹤報價回覆', nextFollowUpAt: '2026-05-01T09:00:00' }),
            ]);
            setMessage('銷售資料已建立 / Sales data created');
            await loadData();
        } catch { setMessage('建立銷售資料失敗 / Failed to create sales data'); }
    };

    return (
        <Stack spacing={3}>
            <Typography variant="h5">客戶管理</Typography>
            {message && <Alert severity="info">{message}</Alert>}
            <Paper sx={{ p: 3 }}>
                <Typography variant="h6" sx={{ mb: 2 }}>客戶</Typography>
                <Stack direction={{ xs: 'column', md: 'row' }} spacing={2} sx={{ mb: 2 }}>
                    <TextField label="客戶名稱" value={customerName} onChange={(event) => setCustomerName(event.target.value)} />
                    <Button variant="contained" onClick={handleCreateCustomer}>新增客戶</Button>
                    <Button variant="outlined" onClick={handleCreateSalesData} disabled={!firstCustomer}>建立銷售資料</Button>
                </Stack>
                <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1 }}>
                    {customers.map((customer) => <Chip key={customer.id} label={`${customer.name} ${customer.grade}`} />)}
                </Box>
            </Paper>
            <Paper sx={{ p: 3 }}>
                <Typography variant="h6" sx={{ mb: 2 }}>銷售漏斗</Typography>
                <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1 }}>
                    {Object.entries(funnel?.stageCounts ?? {}).map(([stage, count]) => <Chip key={stage} label={`${stage}: ${count}`} />)}
                </Box>
            </Paper>
            <Paper sx={{ p: 3 }}>
                <Typography variant="h6" sx={{ mb: 2 }}>商機與報價</Typography>
                <Typography>商機數：{opportunities.length}</Typography>
                <Typography>報價數：{quotations.length}</Typography>
                <Typography>最新報價總額：{quotations[0]?.totalAmount ?? 0}</Typography>
            </Paper>
            <Paper sx={{ p: 3 }}>
                <Typography variant="h6" sx={{ mb: 2 }}>合約與跟進</Typography>
                <Typography>即將到期合約：{contracts.length}</Typography>
                <Typography>待跟進：{followUps.length}</Typography>
            </Paper>
        </Stack>
    );
};
