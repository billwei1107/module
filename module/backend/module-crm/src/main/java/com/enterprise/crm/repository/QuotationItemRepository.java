package com.enterprise.crm.repository;

import com.enterprise.crm.entity.QuotationItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

/**
 * @file QuotationItemRepository.java
 * @description 報價項目資料存取 / Quotation item repository
 */
@Repository
public interface QuotationItemRepository extends JpaRepository<QuotationItem, UUID> {
    List<QuotationItem> findByQuotationIdAndDeletedAtIsNull(UUID quotationId);
}
