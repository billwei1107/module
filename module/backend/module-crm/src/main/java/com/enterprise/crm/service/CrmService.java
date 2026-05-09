package com.enterprise.crm.service;

import com.enterprise.crm.dto.CrmDTOs.*;
import java.util.List;

/**
 * @file CrmService.java
 * @description 客戶管理服務介面 / CRM service contract
 */
public interface CrmService {
    CustomerDTO createCustomer(CreateCustomerRequest request);
    List<CustomerDTO> getCustomers();
    ContactDTO createContact(CreateContactRequest request);
    List<ContactDTO> getContacts(String customerId);
    OpportunityDTO createOpportunity(CreateOpportunityRequest request);
    List<OpportunityDTO> getOpportunities();
    FunnelStatsDTO getFunnelStats();
    QuotationDTO createQuotation(CreateQuotationRequest request);
    List<QuotationDTO> getQuotations();
    ContractDTO createContract(CreateContractRequest request);
    List<ContractDTO> getExpiringContracts(int days);
    InteractionLogDTO createInteractionLog(CreateInteractionLogRequest request);
    List<InteractionLogDTO> getPendingFollowUps();
}
