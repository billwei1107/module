/**
 * @file index.ts
 * @description 客戶管理型別定義 / CRM type definitions
 * @description_en Defines CRM module API data structures for smoke verification UI
 * @description_zh 定義客戶管理模組驗證頁使用的 API 資料結構
 */

export type OpportunityStage = 'LEAD' | 'QUALIFIED' | 'PROPOSAL' | 'NEGOTIATION' | 'CLOSED_WON' | 'CLOSED_LOST';

export interface Customer { id: string; name: string; type: 'COMPANY' | 'INDIVIDUAL'; grade: 'VIP' | 'REGULAR' | 'PROSPECT'; ownerId?: string; phone?: string; email?: string; address?: string; }
export interface Contact { id: string; customerId: string; name: string; title?: string; phone?: string; email?: string; primaryContact: boolean; }
export interface Opportunity { id: string; customerId: string; name: string; stage: OpportunityStage; amount: number; expectedCloseDate?: string; ownerId?: string; }
export interface QuotationItem { id: string; quotationId: string; itemName: string; quantity: number; unitPrice: number; amount: number; }
export interface Quotation { id: string; quotationNo: string; customerId: string; quoteDate: string; taxInclusive: boolean; taxRate: number; subtotal: number; taxAmount: number; totalAmount: number; status: string; items: QuotationItem[]; }
export interface Contract { id: string; contractNo: string; customerId: string; title?: string; startDate: string; endDate: string; amount: number; status: string; }
export interface InteractionLog { id: string; customerId: string; type: string; handledBy?: string; note: string; nextFollowUpAt?: string; completed: boolean; }
export interface FunnelStats { stageCounts: Record<OpportunityStage, number>; }
