/**
 * @file crmApi.ts
 * @description 客戶管理 API 請求層 / CRM API request layer
 * @description_en Wraps CRM customer, pipeline, quotation, contract, and follow-up endpoints
 * @description_zh 封裝客戶、漏斗、報價、合約與跟進 REST 呼叫
 */

import axiosInstance from '../../../shared/api/axiosInstance';
import type { Contact, Contract, Customer, FunnelStats, InteractionLog, Opportunity, Quotation } from '../types';

interface ApiEnvelope<T> { data: T; }
const unwrapData = <T>(response: ApiEnvelope<T>): T => {
    if (!response || response.data === undefined) throw new Error('Invalid API response');
    return response.data;
};

export const getCustomers = async (): Promise<Customer[]> => unwrapData(await axiosInstance.get('/api/v1/crm/customers') as ApiEnvelope<Customer[]>);
export const createCustomer = async (data: { name: string; type: string; grade: string; ownerId: string; phone: string; email: string; address: string }): Promise<Customer> => unwrapData(await axiosInstance.post('/api/v1/crm/customers', data) as ApiEnvelope<Customer>);
export const createContact = async (data: { customerId: string; name: string; title: string; phone: string; email: string; primaryContact: boolean }): Promise<Contact> => unwrapData(await axiosInstance.post('/api/v1/crm/contacts', data) as ApiEnvelope<Contact>);
export const getOpportunities = async (): Promise<Opportunity[]> => unwrapData(await axiosInstance.get('/api/v1/crm/opportunities') as ApiEnvelope<Opportunity[]>);
export const createOpportunity = async (data: { customerId: string; name: string; stage: string; amount: number; expectedCloseDate: string; ownerId: string }): Promise<Opportunity> => unwrapData(await axiosInstance.post('/api/v1/crm/opportunities', data) as ApiEnvelope<Opportunity>);
export const getFunnelStats = async (): Promise<FunnelStats> => unwrapData(await axiosInstance.get('/api/v1/crm/funnel') as ApiEnvelope<FunnelStats>);
export const getQuotations = async (): Promise<Quotation[]> => unwrapData(await axiosInstance.get('/api/v1/crm/quotations') as ApiEnvelope<Quotation[]>);
export const createQuotation = async (data: { quotationNo: string; customerId: string; quoteDate: string; taxInclusive: boolean; taxRate: number; items: Array<{ itemName: string; quantity: number; unitPrice: number }> }): Promise<Quotation> => unwrapData(await axiosInstance.post('/api/v1/crm/quotations', data) as ApiEnvelope<Quotation>);
export const createContract = async (data: { contractNo: string; customerId: string; title: string; startDate: string; endDate: string; amount: number; status: string }): Promise<Contract> => unwrapData(await axiosInstance.post('/api/v1/crm/contracts', data) as ApiEnvelope<Contract>);
export const getExpiringContracts = async (): Promise<Contract[]> => unwrapData(await axiosInstance.get('/api/v1/crm/contracts/expiring') as ApiEnvelope<Contract[]>);
export const createInteractionLog = async (data: { customerId: string; type: string; handledBy: string; note: string; nextFollowUpAt: string }): Promise<InteractionLog> => unwrapData(await axiosInstance.post('/api/v1/crm/interactions', data) as ApiEnvelope<InteractionLog>);
export const getPendingFollowUps = async (): Promise<InteractionLog[]> => unwrapData(await axiosInstance.get('/api/v1/crm/interactions/follow-ups') as ApiEnvelope<InteractionLog[]>);
