package com.enterprise.finance.controller;

import com.enterprise.common.dto.ApiResponse;
import com.enterprise.finance.dto.AgingBucketDTO;
import com.enterprise.finance.dto.CreateInvoiceRequest;
import com.enterprise.finance.dto.InvoiceDTO;
import com.enterprise.finance.dto.PaymentRequest;
import com.enterprise.finance.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @file InvoiceController.java
 * @description 發票控制器 / Invoice controller
 */
@RestController
@RequestMapping("/api/v1/finance/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping
    public ApiResponse<List<InvoiceDTO>> getInvoices() {
        return ApiResponse.success(invoiceService.getInvoices());
    }

    @PostMapping
    public ApiResponse<InvoiceDTO> createInvoice(@RequestBody CreateInvoiceRequest request) {
        return ApiResponse.success(invoiceService.createInvoice(request));
    }

    @PostMapping("/{id}/payments")
    public ApiResponse<InvoiceDTO> recordPayment(@PathVariable String id, @RequestBody PaymentRequest request) {
        return ApiResponse.success(invoiceService.recordPayment(id, request));
    }

    @GetMapping("/aging")
    public ApiResponse<List<AgingBucketDTO>> getAgingAnalysis() {
        return ApiResponse.success(invoiceService.getAgingAnalysis());
    }
}
