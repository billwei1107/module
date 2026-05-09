package com.enterprise.crm.repository;

import com.enterprise.crm.entity.Quotation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

/**
 * @file QuotationRepository.java
 * @description 報價單資料存取 / Quotation repository
 */
@Repository
public interface QuotationRepository extends JpaRepository<Quotation, UUID> {
    List<Quotation> findByDeletedAtIsNullOrderByQuoteDateDesc();
}
