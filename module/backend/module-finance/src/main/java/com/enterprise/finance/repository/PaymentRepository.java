package com.enterprise.finance.repository;

import com.enterprise.finance.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * @file PaymentRepository.java
 * @description 收付款資料存取 / Payment repository
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    List<Payment> findByInvoiceIdAndDeletedAtIsNull(UUID invoiceId);
}
