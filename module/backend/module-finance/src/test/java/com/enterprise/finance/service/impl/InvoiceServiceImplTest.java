package com.enterprise.finance.service.impl;

import com.enterprise.finance.entity.Invoice;
import com.enterprise.finance.entity.Invoice.InvoiceStatus;
import com.enterprise.finance.repository.InvoiceRepository;
import com.enterprise.finance.repository.PaymentRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @file InvoiceServiceImplTest.java
 * @description 發票服務測試 / Invoice service tests
 */
class InvoiceServiceImplTest {

    @Test
    void getAgingAnalysisShouldGroupOutstandingAmountsByOverdueDays() {
        InvoiceRepository invoiceRepository = mock(InvoiceRepository.class);
        Invoice recent = invoice("1000.00", "200.00", LocalDate.now().minusDays(10));
        Invoice old = invoice("500.00", "0.00", LocalDate.now().minusDays(130));
        when(invoiceRepository.findByStatusInAndDeletedAtIsNull(List.of(InvoiceStatus.ISSUED, InvoiceStatus.OVERDUE)))
                .thenReturn(List.of(recent, old));

        InvoiceServiceImpl service = new InvoiceServiceImpl(invoiceRepository, mock(PaymentRepository.class));

        assertThat(service.getAgingAnalysis())
                .anySatisfy(bucket -> {
                    if ("0-30".equals(bucket.getBucket())) {
                        assertThat(bucket.getAmount()).isEqualByComparingTo(new BigDecimal("800.00"));
                    }
                })
                .anySatisfy(bucket -> {
                    if ("120+".equals(bucket.getBucket())) {
                        assertThat(bucket.getAmount()).isEqualByComparingTo(new BigDecimal("500.00"));
                    }
                });
    }

    private Invoice invoice(String amount, String paidAmount, LocalDate dueDate) {
        Invoice invoice = new Invoice();
        invoice.setAmount(new BigDecimal(amount));
        invoice.setPaidAmount(new BigDecimal(paidAmount));
        invoice.setDueDate(dueDate);
        invoice.setStatus(InvoiceStatus.ISSUED);
        return invoice;
    }
}
