/**
 * @file inventoryApi.ts
 * @description 庫存管理 API 請求層 / Inventory API request layer
 * @description_en Wraps inventory setup, movement, stock take, and report endpoints
 * @description_zh 封裝庫存設定、異動、盤點與報表 REST 呼叫
 */

import axiosInstance from '../../../shared/api/axiosInstance';
import type { InventoryReport, Item, MovementType, StockMovement, StockRecord, StockTake, Warehouse } from '../types';

interface ApiEnvelope<T> { data: T; }
const unwrapData = <T>(response: ApiEnvelope<T>): T => {
    if (!response || response.data === undefined) throw new Error('Invalid API response');
    return response.data;
};

export const createWarehouse = async (data: { code: string; name: string; location: string }): Promise<Warehouse> => unwrapData(await axiosInstance.post('/api/v1/inventory/warehouses', data) as ApiEnvelope<Warehouse>);
export const getItems = async (): Promise<Item[]> => unwrapData(await axiosInstance.get('/api/v1/inventory/items') as ApiEnvelope<Item[]>);
export const createItem = async (data: { sku: string; name: string; specification: string; barcode: string; unit: string; safetyStock: number }): Promise<Item> => unwrapData(await axiosInstance.post('/api/v1/inventory/items', data) as ApiEnvelope<Item>);
export const recordMovement = async (data: { itemId: string; fromWarehouseId?: string; toWarehouseId?: string; type: MovementType; quantity: number; referenceNo: string; note: string }): Promise<StockMovement> => unwrapData(await axiosInstance.post('/api/v1/inventory/movements', data) as ApiEnvelope<StockMovement>);
export const getStockRecords = async (): Promise<StockRecord[]> => unwrapData(await axiosInstance.get('/api/v1/inventory/records') as ApiEnvelope<StockRecord[]>);
export const freezeStockTake = async (itemId: string, warehouseId: string): Promise<StockTake> => unwrapData(await axiosInstance.post('/api/v1/inventory/stock-takes/freeze', null, { params: { itemId, warehouseId } }) as ApiEnvelope<StockTake>);
export const countStockTake = async (id: string, actualQuantity: number): Promise<StockTake> => unwrapData(await axiosInstance.post(`/api/v1/inventory/stock-takes/${id}/count`, { actualQuantity }) as ApiEnvelope<StockTake>);
export const adjustStockTake = async (id: string): Promise<StockTake> => unwrapData(await axiosInstance.post(`/api/v1/inventory/stock-takes/${id}/adjust`) as ApiEnvelope<StockTake>);
export const getInventoryReport = async (): Promise<InventoryReport> => unwrapData(await axiosInstance.get('/api/v1/inventory/report') as ApiEnvelope<InventoryReport>);
