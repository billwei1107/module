package com.enterprise.crm.service.impl;

import com.enterprise.crm.dto.CrmDTOs.*;
import com.enterprise.crm.entity.Customer;
import com.enterprise.crm.entity.Opportunity;
import com.enterprise.crm.repository.*;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @file CrmServiceImplTest.java
 * @description 客戶管理服務測試 / CRM service tests
 */
class CrmServiceImplTest {

    @Test
    void createQuotationShouldCalculateExclusiveTaxTotals() {
        CustomerRepository customerRepository = mock(CustomerRepository.class);
        QuotationRepository quotationRepository = mock(QuotationRepository.class);
        QuotationItemRepository itemRepository = mock(QuotationItemRepository.class);
        UUID customerId = UUID.randomUUID();
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer(customerId)));
        when(quotationRepository.save(any())).thenAnswer(invocation -> {
            var quotation = invocation.getArgument(0, com.enterprise.crm.entity.Quotation.class);
            if (quotation.getId() == null) quotation.setId(UUID.randomUUID());
            return quotation;
        });
        when(itemRepository.save(any())).thenAnswer(invocation -> {
            var item = invocation.getArgument(0, com.enterprise.crm.entity.QuotationItem.class);
            item.setId(UUID.randomUUID());
            return item;
        });
        when(itemRepository.findByQuotationIdAndDeletedAtIsNull(any())).thenReturn(java.util.List.of());
        CrmServiceImpl service = service(customerRepository, mock(OpportunityRepository.class), quotationRepository, itemRepository);

        CreateQuotationRequest request = new CreateQuotationRequest();
        request.setQuotationNo("QT-001");
        request.setCustomerId(customerId.toString());
        request.setTaxRate(new BigDecimal("0.05"));
        request.setQuoteDate(LocalDate.of(2026, 5, 9));
        QuotationItemRequest item = new QuotationItemRequest();
        item.setItemName("導入服務");
        item.setQuantity(new BigDecimal("2"));
        item.setUnitPrice(new BigDecimal("100"));
        request.setItems(java.util.List.of(item));

        assertThat(service.createQuotation(request).getSubtotal()).isEqualByComparingTo("200.0000");
        assertThat(service.createQuotation(request).getTaxAmount()).isEqualByComparingTo("10.0000");
        assertThat(service.createQuotation(request).getTotalAmount()).isEqualByComparingTo("210.0000");
    }

    @Test
    void funnelStatsShouldReturnAllStages() {
        OpportunityRepository opportunityRepository = mock(OpportunityRepository.class);
        when(opportunityRepository.countByStageAndDeletedAtIsNull(Opportunity.Stage.LEAD)).thenReturn(2L);
        CrmServiceImpl service = service(mock(CustomerRepository.class), opportunityRepository, mock(QuotationRepository.class), mock(QuotationItemRepository.class));

        assertThat(service.getFunnelStats().getStageCounts()).containsEntry(Opportunity.Stage.LEAD, 2L);
        assertThat(service.getFunnelStats().getStageCounts()).containsKey(Opportunity.Stage.CLOSED_WON);
    }

    private CrmServiceImpl service(CustomerRepository customerRepository, OpportunityRepository opportunityRepository, QuotationRepository quotationRepository, QuotationItemRepository itemRepository) {
        return new CrmServiceImpl(customerRepository, mock(ContactRepository.class), opportunityRepository, quotationRepository, itemRepository, mock(ContractRepository.class), mock(InteractionLogRepository.class));
    }

    private Customer customer(UUID id) {
        Customer customer = new Customer();
        customer.setId(id);
        customer.setName("BillW");
        return customer;
    }
}
