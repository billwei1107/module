package com.enterprise.crm.service.impl;

import com.enterprise.common.annotation.Auditable;
import com.enterprise.common.exception.BusinessException;
import com.enterprise.crm.dto.CrmDTOs.*;
import com.enterprise.crm.entity.*;
import com.enterprise.crm.entity.Opportunity.Stage;
import com.enterprise.crm.repository.*;
import com.enterprise.crm.service.CrmService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @file CrmServiceImpl.java
 * @description 客戶管理服務實作 / CRM service implementation
 * @description_en Handles customers, contacts, pipeline, quotations, contracts, and follow-ups
 * @description_zh 處理客戶、聯絡人、銷售漏斗、報價、合約與跟進提醒
 */
@Service
@RequiredArgsConstructor
public class CrmServiceImpl implements CrmService {
    private final CustomerRepository customerRepository;
    private final ContactRepository contactRepository;
    private final OpportunityRepository opportunityRepository;
    private final QuotationRepository quotationRepository;
    private final QuotationItemRepository quotationItemRepository;
    private final ContractRepository contractRepository;
    private final InteractionLogRepository interactionLogRepository;

    @Override
    @Transactional
    @Auditable(module = "crm", action = "CREATE_CUSTOMER")
    public CustomerDTO createCustomer(CreateCustomerRequest request) {
        Customer customer = new Customer();
        customer.setName(required(request.getName(), "客戶名稱不可為空 / Customer name is required"));
        customer.setType(request.getType());
        customer.setGrade(request.getGrade());
        customer.setOwnerId(request.getOwnerId());
        customer.setPhone(request.getPhone());
        customer.setEmail(request.getEmail());
        customer.setAddress(request.getAddress());
        return toDTO(customerRepository.save(customer));
    }

    @Override
    public List<CustomerDTO> getCustomers() {
        return customerRepository.findByDeletedAtIsNullOrderByCreatedAtDesc().stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional
    public ContactDTO createContact(CreateContactRequest request) {
        Customer customer = findCustomer(request.getCustomerId());
        Contact contact = new Contact();
        contact.setCustomerId(customer.getId());
        contact.setName(required(request.getName(), "聯絡人名稱不可為空 / Contact name is required"));
        contact.setTitle(request.getTitle());
        contact.setPhone(request.getPhone());
        contact.setEmail(request.getEmail());
        contact.setPrimaryContact(Boolean.TRUE.equals(request.getPrimaryContact()));
        return toDTO(contactRepository.save(contact));
    }

    @Override
    public List<ContactDTO> getContacts(String customerId) {
        return contactRepository.findByCustomerIdAndDeletedAtIsNullOrderByPrimaryContactDescNameAsc(findCustomer(customerId).getId()).stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional
    public OpportunityDTO createOpportunity(CreateOpportunityRequest request) {
        Customer customer = findCustomer(request.getCustomerId());
        Opportunity opportunity = new Opportunity();
        opportunity.setCustomerId(customer.getId());
        opportunity.setName(required(request.getName(), "商機名稱不可為空 / Opportunity name is required"));
        opportunity.setStage(request.getStage() == null ? Stage.LEAD : request.getStage());
        opportunity.setAmount(request.getAmount() == null ? BigDecimal.ZERO : request.getAmount());
        opportunity.setExpectedCloseDate(request.getExpectedCloseDate());
        opportunity.setOwnerId(request.getOwnerId());
        return toDTO(opportunityRepository.save(opportunity));
    }

    @Override
    public List<OpportunityDTO> getOpportunities() {
        return opportunityRepository.findByDeletedAtIsNullOrderByCreatedAtDesc().stream().map(this::toDTO).toList();
    }

    @Override
    public FunnelStatsDTO getFunnelStats() {
        Map<Stage, Long> counts = new EnumMap<>(Stage.class);
        for (Stage stage : Stage.values()) {
            counts.put(stage, opportunityRepository.countByStageAndDeletedAtIsNull(stage));
        }
        return FunnelStatsDTO.builder().stageCounts(counts).build();
    }

    @Override
    @Transactional
    @Auditable(module = "crm", action = "CREATE_QUOTATION")
    public QuotationDTO createQuotation(CreateQuotationRequest request) {
        Customer customer = findCustomer(request.getCustomerId());
        Quotation quotation = new Quotation();
        quotation.setQuotationNo(required(request.getQuotationNo(), "報價單號不可為空 / Quotation number is required"));
        quotation.setCustomerId(customer.getId());
        quotation.setOpportunityId(parseUuid(request.getOpportunityId()));
        quotation.setQuoteDate(request.getQuoteDate() == null ? LocalDate.now() : request.getQuoteDate());
        quotation.setTaxInclusive(Boolean.TRUE.equals(request.getTaxInclusive()));
        quotation.setTaxRate(request.getTaxRate() == null ? BigDecimal.ZERO : request.getTaxRate());
        Quotation saved = quotationRepository.save(quotation);
        BigDecimal lineTotal = BigDecimal.ZERO;
        for (QuotationItemRequest itemRequest : request.getItems() == null ? List.<QuotationItemRequest>of() : request.getItems()) {
            QuotationItem item = new QuotationItem();
            item.setQuotationId(saved.getId());
            item.setItemName(required(itemRequest.getItemName(), "報價項目不可為空 / Quotation item is required"));
            item.setQuantity(itemRequest.getQuantity() == null ? BigDecimal.ZERO : itemRequest.getQuantity());
            item.setUnitPrice(itemRequest.getUnitPrice() == null ? BigDecimal.ZERO : itemRequest.getUnitPrice());
            item.setAmount(item.getQuantity().multiply(item.getUnitPrice()).setScale(4, RoundingMode.HALF_UP));
            lineTotal = lineTotal.add(item.getAmount());
            quotationItemRepository.save(item);
        }
        applyQuotationTotals(saved, lineTotal);
        return toDTO(quotationRepository.save(saved));
    }

    @Override
    public List<QuotationDTO> getQuotations() {
        return quotationRepository.findByDeletedAtIsNullOrderByQuoteDateDesc().stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional
    public ContractDTO createContract(CreateContractRequest request) {
        Customer customer = findCustomer(request.getCustomerId());
        Contract contract = new Contract();
        contract.setContractNo(required(request.getContractNo(), "合約編號不可為空 / Contract number is required"));
        contract.setCustomerId(customer.getId());
        contract.setTitle(request.getTitle());
        contract.setStartDate(request.getStartDate());
        contract.setEndDate(request.getEndDate());
        contract.setAmount(request.getAmount() == null ? BigDecimal.ZERO : request.getAmount());
        contract.setStatus(request.getStatus() == null ? Contract.ContractStatus.ACTIVE : request.getStatus());
        return toDTO(contractRepository.save(contract));
    }

    @Override
    public List<ContractDTO> getExpiringContracts(int days) {
        return contractRepository.findByEndDateBetweenAndDeletedAtIsNullOrderByEndDateAsc(LocalDate.now(), LocalDate.now().plusDays(days)).stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional
    public InteractionLogDTO createInteractionLog(CreateInteractionLogRequest request) {
        Customer customer = findCustomer(request.getCustomerId());
        InteractionLog log = new InteractionLog();
        log.setCustomerId(customer.getId());
        log.setContactId(parseUuid(request.getContactId()));
        log.setType(request.getType());
        log.setHandledBy(request.getHandledBy());
        log.setNote(required(request.getNote(), "互動紀錄不可為空 / Interaction note is required"));
        log.setNextFollowUpAt(request.getNextFollowUpAt());
        return toDTO(interactionLogRepository.save(log));
    }

    @Override
    public List<InteractionLogDTO> getPendingFollowUps() {
        return interactionLogRepository.findByNextFollowUpAtBeforeAndCompletedFalseAndDeletedAtIsNullOrderByNextFollowUpAtAsc(LocalDateTime.now()).stream().map(this::toDTO).toList();
    }

    private void applyQuotationTotals(Quotation quotation, BigDecimal lineTotal) {
        BigDecimal rate = quotation.getTaxRate();
        if (Boolean.TRUE.equals(quotation.getTaxInclusive())) {
            BigDecimal divisor = BigDecimal.ONE.add(rate);
            BigDecimal subtotal = divisor.compareTo(BigDecimal.ZERO) == 0 ? lineTotal : lineTotal.divide(divisor, 4, RoundingMode.HALF_UP);
            quotation.setSubtotal(subtotal);
            quotation.setTaxAmount(lineTotal.subtract(subtotal).setScale(4, RoundingMode.HALF_UP));
            quotation.setTotalAmount(lineTotal.setScale(4, RoundingMode.HALF_UP));
        } else {
            quotation.setSubtotal(lineTotal.setScale(4, RoundingMode.HALF_UP));
            quotation.setTaxAmount(lineTotal.multiply(rate).setScale(4, RoundingMode.HALF_UP));
            quotation.setTotalAmount(quotation.getSubtotal().add(quotation.getTaxAmount()).setScale(4, RoundingMode.HALF_UP));
        }
    }

    private Customer findCustomer(String id) {
        return customerRepository.findById(UUID.fromString(id)).filter(customer -> customer.getDeletedAt() == null)
                .orElseThrow(() -> new BusinessException(404, "客戶不存在 / Customer not found"));
    }

    private UUID parseUuid(String value) { return value == null || value.isBlank() ? null : UUID.fromString(value); }
    private String required(String value, String message) {
        if (value == null || value.isBlank()) throw new BusinessException(400, message);
        return value.trim();
    }

    private CustomerDTO toDTO(Customer c) { return CustomerDTO.builder().id(c.getId().toString()).name(c.getName()).type(c.getType()).grade(c.getGrade()).ownerId(c.getOwnerId()).phone(c.getPhone()).email(c.getEmail()).address(c.getAddress()).build(); }
    private ContactDTO toDTO(Contact c) { return ContactDTO.builder().id(c.getId().toString()).customerId(c.getCustomerId().toString()).name(c.getName()).title(c.getTitle()).phone(c.getPhone()).email(c.getEmail()).primaryContact(c.getPrimaryContact()).build(); }
    private OpportunityDTO toDTO(Opportunity o) { return OpportunityDTO.builder().id(o.getId().toString()).customerId(o.getCustomerId().toString()).name(o.getName()).stage(o.getStage()).amount(o.getAmount()).expectedCloseDate(o.getExpectedCloseDate()).ownerId(o.getOwnerId()).build(); }
    private QuotationItemDTO toDTO(QuotationItem i) { return QuotationItemDTO.builder().id(i.getId().toString()).quotationId(i.getQuotationId().toString()).itemName(i.getItemName()).quantity(i.getQuantity()).unitPrice(i.getUnitPrice()).amount(i.getAmount()).build(); }
    private QuotationDTO toDTO(Quotation q) { return QuotationDTO.builder().id(q.getId().toString()).quotationNo(q.getQuotationNo()).customerId(q.getCustomerId().toString()).opportunityId(q.getOpportunityId() == null ? null : q.getOpportunityId().toString()).quoteDate(q.getQuoteDate()).taxInclusive(q.getTaxInclusive()).taxRate(q.getTaxRate()).subtotal(q.getSubtotal()).taxAmount(q.getTaxAmount()).totalAmount(q.getTotalAmount()).status(q.getStatus()).items(quotationItemRepository.findByQuotationIdAndDeletedAtIsNull(q.getId()).stream().map(this::toDTO).toList()).build(); }
    private ContractDTO toDTO(Contract c) { return ContractDTO.builder().id(c.getId().toString()).contractNo(c.getContractNo()).customerId(c.getCustomerId().toString()).title(c.getTitle()).startDate(c.getStartDate()).endDate(c.getEndDate()).amount(c.getAmount()).status(c.getStatus()).build(); }
    private InteractionLogDTO toDTO(InteractionLog l) { return InteractionLogDTO.builder().id(l.getId().toString()).customerId(l.getCustomerId().toString()).contactId(l.getContactId() == null ? null : l.getContactId().toString()).type(l.getType()).handledBy(l.getHandledBy()).note(l.getNote()).nextFollowUpAt(l.getNextFollowUpAt()).completed(l.getCompleted()).build(); }
}
