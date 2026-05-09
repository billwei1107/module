package com.enterprise.crm.controller;

import com.enterprise.common.dto.ApiResponse;
import com.enterprise.crm.dto.CrmDTOs.*;
import com.enterprise.crm.service.CrmService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * @file CrmController.java
 * @description 客戶管理控制器 / CRM controller
 */
@RestController
@RequestMapping("/api/v1/crm")
@RequiredArgsConstructor
public class CrmController {
    private final CrmService crmService;

    @GetMapping("/customers") public ApiResponse<List<CustomerDTO>> getCustomers() { return ApiResponse.success(crmService.getCustomers()); }
    @PostMapping("/customers") public ApiResponse<CustomerDTO> createCustomer(@RequestBody CreateCustomerRequest request) { return ApiResponse.success(crmService.createCustomer(request)); }
    @GetMapping("/customers/{customerId}/contacts") public ApiResponse<List<ContactDTO>> getContacts(@PathVariable String customerId) { return ApiResponse.success(crmService.getContacts(customerId)); }
    @PostMapping("/contacts") public ApiResponse<ContactDTO> createContact(@RequestBody CreateContactRequest request) { return ApiResponse.success(crmService.createContact(request)); }
    @GetMapping("/opportunities") public ApiResponse<List<OpportunityDTO>> getOpportunities() { return ApiResponse.success(crmService.getOpportunities()); }
    @PostMapping("/opportunities") public ApiResponse<OpportunityDTO> createOpportunity(@RequestBody CreateOpportunityRequest request) { return ApiResponse.success(crmService.createOpportunity(request)); }
    @GetMapping("/funnel") public ApiResponse<FunnelStatsDTO> getFunnelStats() { return ApiResponse.success(crmService.getFunnelStats()); }
    @GetMapping("/quotations") public ApiResponse<List<QuotationDTO>> getQuotations() { return ApiResponse.success(crmService.getQuotations()); }
    @PostMapping("/quotations") public ApiResponse<QuotationDTO> createQuotation(@RequestBody CreateQuotationRequest request) { return ApiResponse.success(crmService.createQuotation(request)); }
    @PostMapping("/contracts") public ApiResponse<ContractDTO> createContract(@RequestBody CreateContractRequest request) { return ApiResponse.success(crmService.createContract(request)); }
    @GetMapping("/contracts/expiring") public ApiResponse<List<ContractDTO>> getExpiringContracts(@RequestParam(defaultValue = "30") int days) { return ApiResponse.success(crmService.getExpiringContracts(days)); }
    @PostMapping("/interactions") public ApiResponse<InteractionLogDTO> createInteractionLog(@RequestBody CreateInteractionLogRequest request) { return ApiResponse.success(crmService.createInteractionLog(request)); }
    @GetMapping("/interactions/follow-ups") public ApiResponse<List<InteractionLogDTO>> getPendingFollowUps() { return ApiResponse.success(crmService.getPendingFollowUps()); }
}
