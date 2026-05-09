package com.enterprise.finance.service.impl;

import com.enterprise.common.annotation.Auditable;
import com.enterprise.common.exception.BusinessException;
import com.enterprise.finance.dto.AgingBucketDTO;
import com.enterprise.finance.dto.CreateInvoiceRequest;
import com.enterprise.finance.dto.InvoiceDTO;
import com.enterprise.finance.dto.PaymentRequest;
import com.enterprise.finance.entity.Invoice;
import com.enterprise.finance.entity.Invoice.InvoiceStatus;
import com.enterprise.finance.entity.Payment;
import com.enterprise.finance.repository.InvoiceRepository;
import com.enterprise.finance.repository.PaymentRepository;
import com.enterprise.finance.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @file InvoiceServiceImpl.java
 * @description 發票服務實作 / Invoice service implementation
 * @description_en Tracks invoice payment state and aging buckets using BigDecimal amounts
 * @description_zh 使用 BigDecimal 追蹤發票付款狀態與帳齡區間
 */
@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;

    @Override
    @Transactional
    @Auditable(module = "finance", action = "CREATE_INVOICE")
    public InvoiceDTO createInvoice(CreateInvoiceRequest request) {
        Invoice invoice = new Invoice();
        invoice.setInvoiceNo(request.getInvoiceNo());
        invoice.setType(request.getType());
        invoice.setCounterpartyId(request.getCounterpartyId());
        invoice.setAmount(safeAmount(request.getAmount()));
        invoice.setDueDate(request.getDueDate());
        invoice.setStatus(InvoiceStatus.ISSUED);
        return toDTO(invoiceRepository.save(invoice));
    }

    @Override
    @Transactional
    @Auditable(module = "finance", action = "RECORD_PAYMENT")
    public InvoiceDTO recordPayment(String invoiceId, PaymentRequest request) {
        Invoice invoice = invoiceRepository.findById(UUID.fromString(invoiceId))
                .orElseThrow(() -> new BusinessException(404, "發票不存在 / Invoice not found"));
        Payment payment = new Payment();
        payment.setInvoiceId(invoice.getId());
        payment.setAmount(safeAmount(request.getAmount()));
        payment.setPaymentDate(request.getPaymentDate() == null ? LocalDate.now() : request.getPaymentDate());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setReferenceNo(request.getReferenceNo());
        paymentRepository.save(payment);

        BigDecimal paidAmount = safeAmount(invoice.getPaidAmount()).add(payment.getAmount());
        invoice.setPaidAmount(paidAmount);
        if (paidAmount.compareTo(invoice.getAmount()) >= 0) {
            invoice.setStatus(InvoiceStatus.PAID);
        }
        return toDTO(invoiceRepository.save(invoice));
    }

    @Override
    public List<InvoiceDTO> getInvoices() {
        return invoiceRepository.findByDeletedAtIsNullOrderByDueDateAsc().stream().map(this::toDTO).toList();
    }

    @Override
    public List<AgingBucketDTO> getAgingAnalysis() {
        Map<String, BigDecimal> buckets = new LinkedHashMap<>();
        buckets.put("0-30", BigDecimal.ZERO);
        buckets.put("31-60", BigDecimal.ZERO);
        buckets.put("61-90", BigDecimal.ZERO);
        buckets.put("91-120", BigDecimal.ZERO);
        buckets.put("120+", BigDecimal.ZERO);
        LocalDate today = LocalDate.now();
        invoiceRepository.findByStatusInAndDeletedAtIsNull(List.of(InvoiceStatus.ISSUED, InvoiceStatus.OVERDUE)).forEach(invoice -> {
            long overdueDays = Math.max(0, ChronoUnit.DAYS.between(invoice.getDueDate(), today));
            String bucket = bucket(overdueDays);
            BigDecimal outstanding = safeAmount(invoice.getAmount()).subtract(safeAmount(invoice.getPaidAmount()));
            buckets.put(bucket, buckets.get(bucket).add(outstanding));
        });
        return buckets.entrySet().stream()
                .map(entry -> AgingBucketDTO.builder().bucket(entry.getKey()).amount(entry.getValue()).build())
                .toList();
    }

    private String bucket(long overdueDays) {
        if (overdueDays <= 30) return "0-30";
        if (overdueDays <= 60) return "31-60";
        if (overdueDays <= 90) return "61-90";
        if (overdueDays <= 120) return "91-120";
        return "120+";
    }

    private BigDecimal safeAmount(BigDecimal amount) {
        return amount == null ? BigDecimal.ZERO : amount;
    }

    private InvoiceDTO toDTO(Invoice invoice) {
        return InvoiceDTO.builder()
                .id(invoice.getId() != null ? invoice.getId().toString() : null)
                .invoiceNo(invoice.getInvoiceNo())
                .type(invoice.getType())
                .counterpartyId(invoice.getCounterpartyId())
                .amount(invoice.getAmount())
                .paidAmount(invoice.getPaidAmount())
                .dueDate(invoice.getDueDate())
                .status(invoice.getStatus())
                .build();
    }
}
