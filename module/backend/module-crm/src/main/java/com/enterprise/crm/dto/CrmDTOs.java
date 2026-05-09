package com.enterprise.crm.dto;

import com.enterprise.crm.entity.Contract.ContractStatus;
import com.enterprise.crm.entity.Customer.CustomerGrade;
import com.enterprise.crm.entity.Customer.CustomerType;
import com.enterprise.crm.entity.InteractionLog.InteractionType;
import com.enterprise.crm.entity.Opportunity.Stage;
import com.enterprise.crm.entity.Quotation.QuotationStatus;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @file CrmDTOs.java
 * @description 客戶管理 DTO 集合 / CRM DTO collection
 * @description_en Defines CRM request and response data structures
 * @description_zh 定義客戶管理模組請求與回傳資料結構
 */
public final class CrmDTOs {
    private CrmDTOs() {}

    @Data public static class CreateCustomerRequest { private String name; private CustomerType type = CustomerType.COMPANY; private CustomerGrade grade = CustomerGrade.PROSPECT; private String ownerId; private String phone; private String email; private String address; }
    @Data public static class CreateContactRequest { private String customerId; private String name; private String title; private String phone; private String email; private Boolean primaryContact = false; }
    @Data public static class CreateOpportunityRequest { private String customerId; private String name; private Stage stage = Stage.LEAD; private BigDecimal amount = BigDecimal.ZERO; private LocalDate expectedCloseDate; private String ownerId; }
    @Data public static class QuotationItemRequest { private String itemName; private BigDecimal quantity = BigDecimal.ZERO; private BigDecimal unitPrice = BigDecimal.ZERO; }
    @Data public static class CreateQuotationRequest { private String quotationNo; private String customerId; private String opportunityId; private LocalDate quoteDate; private Boolean taxInclusive = false; private BigDecimal taxRate = BigDecimal.ZERO; private List<QuotationItemRequest> items = List.of(); }
    @Data public static class CreateContractRequest { private String contractNo; private String customerId; private String title; private LocalDate startDate; private LocalDate endDate; private BigDecimal amount = BigDecimal.ZERO; private ContractStatus status = ContractStatus.ACTIVE; }
    @Data public static class CreateInteractionLogRequest { private String customerId; private String contactId; private InteractionType type = InteractionType.FOLLOW_UP; private String handledBy; private String note; private LocalDateTime nextFollowUpAt; }

    @Data @Builder public static class CustomerDTO { private String id; private String name; private CustomerType type; private CustomerGrade grade; private String ownerId; private String phone; private String email; private String address; }
    @Data @Builder public static class ContactDTO { private String id; private String customerId; private String name; private String title; private String phone; private String email; private Boolean primaryContact; }
    @Data @Builder public static class OpportunityDTO { private String id; private String customerId; private String name; private Stage stage; private BigDecimal amount; private LocalDate expectedCloseDate; private String ownerId; }
    @Data @Builder public static class QuotationItemDTO { private String id; private String quotationId; private String itemName; private BigDecimal quantity; private BigDecimal unitPrice; private BigDecimal amount; }
    @Data @Builder public static class QuotationDTO { private String id; private String quotationNo; private String customerId; private String opportunityId; private LocalDate quoteDate; private Boolean taxInclusive; private BigDecimal taxRate; private BigDecimal subtotal; private BigDecimal taxAmount; private BigDecimal totalAmount; private QuotationStatus status; private List<QuotationItemDTO> items; }
    @Data @Builder public static class ContractDTO { private String id; private String contractNo; private String customerId; private String title; private LocalDate startDate; private LocalDate endDate; private BigDecimal amount; private ContractStatus status; }
    @Data @Builder public static class InteractionLogDTO { private String id; private String customerId; private String contactId; private InteractionType type; private String handledBy; private String note; private LocalDateTime nextFollowUpAt; private Boolean completed; }
    @Data @Builder public static class FunnelStatsDTO { private Map<Stage, Long> stageCounts; }
}
