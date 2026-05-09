/**
 * @file index.ts
 * @description 庫存管理型別定義 / Inventory type definitions
 * @description_en Defines inventory module API data structures for smoke verification UI
 * @description_zh 定義庫存管理模組驗證頁使用的 API 資料結構
 */

export type MovementType = 'INBOUND' | 'OUTBOUND' | 'TRANSFER' | 'ADJUSTMENT';

export interface Item { id: string; categoryId?: string; sku: string; name: string; specification?: string; barcode?: string; unit: string; safetyStock: number; }
export interface Warehouse { id: string; code: string; name: string; location?: string; }
export interface StockRecord { id: string; itemId: string; warehouseId: string; quantity: number; }
export interface StockMovement { id: string; itemId: string; fromWarehouseId?: string; toWarehouseId?: string; type: MovementType; quantity: number; referenceNo?: string; note?: string; }
export interface StockTake { id: string; itemId: string; warehouseId: string; expectedQuantity: number; actualQuantity?: number; differenceQuantity?: number; status: 'FROZEN' | 'COUNTED' | 'ADJUSTED'; }
export interface InventoryReport { records: StockRecord[]; lowStockRecords: StockRecord[]; }
