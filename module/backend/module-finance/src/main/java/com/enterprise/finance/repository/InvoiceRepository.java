package com.enterprise.finance.repository;

import com.enterprise.finance.entity.Invoice;
import com.enterprise.finance.entity.Invoice.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @file InvoiceRepository.java
 * @description 發票資料存取 / Invoice repository
 */
@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {
    Optional<Invoice> findByInvoiceNoAndDeletedAtIsNull(String invoiceNo);

    List<Invoice> findByDeletedAtIsNullOrderByDueDateAsc();

    List<Invoice> findByStatusInAndDeletedAtIsNull(List<InvoiceStatus> statuses);
}
