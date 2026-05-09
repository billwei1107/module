/**
 * @file InventoryDashboardPage.tsx
 * @description 庫存管理驗證頁 / Inventory dashboard verification page
 * @description_en Provides a minimal smoke-test UI for inventory module APIs
 * @description_zh 提供母體庫驗證庫存管理 API 的最小頁面
 */

import { useEffect, useMemo, useState } from 'react';
import { Alert, Button, Chip, Paper, Stack, TextField, Typography } from '@mui/material';
import { adjustStockTake, countStockTake, createItem, createWarehouse, freezeStockTake, getInventoryReport, getItems, getStockRecords, recordMovement } from '../api/inventoryApi';
import type { InventoryReport, Item, StockRecord, StockTake, Warehouse } from '../types';

export const InventoryDashboardPage = () => {
    const [items, setItems] = useState<Item[]>([]);
    const [records, setRecords] = useState<StockRecord[]>([]);
    const [report, setReport] = useState<InventoryReport | null>(null);
    const [warehouse, setWarehouse] = useState<Warehouse | null>(null);
    const [stockTake, setStockTake] = useState<StockTake | null>(null);
    const [sku, setSku] = useState('SKU-001');
    const [message, setMessage] = useState('');
    const firstItem = useMemo(() => items[0], [items]);
    const firstRecord = useMemo(() => records[0], [records]);

    const loadData = async () => {
        const [itemResult, recordResult, reportResult] = await Promise.all([getItems(), getStockRecords(), getInventoryReport()]);
        setItems(itemResult);
        setRecords(recordResult);
        setReport(reportResult);
    };

    useEffect(() => { loadData().catch(() => setMessage('載入庫存資料失敗 / Failed to load inventory data')); }, []);

    const handleSetup = async () => {
        try {
            const createdWarehouse = await createWarehouse({ code: `W-${Date.now()}`, name: '主倉', location: 'Taipei' });
            setWarehouse(createdWarehouse);
            await createItem({ sku, name: '測試品項', specification: 'Box', barcode: '471000000001', unit: 'PCS', safetyStock: 5 });
            setMessage('品項與倉庫已建立 / Item and warehouse created');
            await loadData();
        } catch { setMessage('建立品項或倉庫失敗 / Failed to create item or warehouse'); }
    };

    const handleInbound = async () => {
        if (!firstItem || !warehouse) return;
        try {
            await recordMovement({ itemId: firstItem.id, toWarehouseId: warehouse.id, type: 'INBOUND', quantity: 10, referenceNo: 'IN-001', note: '入庫驗證' });
            setMessage('入庫已完成 / Inbound completed');
            await loadData();
        } catch { setMessage('入庫失敗 / Failed to receive stock'); }
    };

    const handleStockTake = async () => {
        if (!firstRecord) return;
        try {
            const frozen = await freezeStockTake(firstRecord.itemId, firstRecord.warehouseId);
            const counted = await countStockTake(frozen.id, 8);
            const adjusted = await adjustStockTake(counted.id);
            setStockTake(adjusted);
            setMessage('盤點已調整 / Stock take adjusted');
            await loadData();
        } catch { setMessage('盤點失敗 / Failed to process stock take'); }
    };

    return (
        <Stack spacing={3}>
            <Typography variant="h5">庫存管理</Typography>
            {message && <Alert severity="info">{message}</Alert>}
            <Paper sx={{ p: 3 }}>
                <Typography variant="h6" sx={{ mb: 2 }}>品項設定</Typography>
                <Stack direction={{ xs: 'column', md: 'row' }} spacing={2}>
                    <TextField label="SKU" value={sku} onChange={(event) => setSku(event.target.value)} />
                    <Button variant="contained" onClick={handleSetup}>建立品項與倉庫</Button>
                    <Button variant="outlined" onClick={handleInbound} disabled={!firstItem || !warehouse}>入庫</Button>
                    <Button variant="outlined" onClick={handleStockTake} disabled={!firstRecord}>盤點調整</Button>
                </Stack>
                <Stack direction="row" spacing={1} sx={{ mt: 2, flexWrap: 'wrap' }}>
                    {items.map((item) => <Chip key={item.id} label={`${item.sku} ${item.name}`} />)}
                </Stack>
            </Paper>
            <Paper sx={{ p: 3 }}>
                <Typography variant="h6" sx={{ mb: 2 }}>庫存紀錄</Typography>
                <Typography>庫存筆數：{records.length}</Typography>
                <Typography>目前數量：{firstRecord?.quantity ?? 0}</Typography>
                <Typography>低庫存筆數：{report?.lowStockRecords.length ?? 0}</Typography>
                <Typography>盤點狀態：{stockTake?.status ?? '尚未盤點'}</Typography>
            </Paper>
        </Stack>
    );
};
