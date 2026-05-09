package com.enterprise.finance.service;

import com.enterprise.finance.dto.AgingBucketDTO;
import com.enterprise.finance.dto.CreateInvoiceRequest;
import com.enterprise.finance.dto.InvoiceDTO;
import com.enterprise.finance.dto.PaymentRequest;

import java.util.List;

/**
 * @file InvoiceService.java
 * @description 發票服務介面 / Invoice service contract
 */
public interface InvoiceService {
    InvoiceDTO createInvoice(CreateInvoiceRequest request);

    InvoiceDTO recordPayment(String invoiceId, PaymentRequest request);

    List<InvoiceDTO> getInvoices();

    List<AgingBucketDTO> getAgingAnalysis();
}
