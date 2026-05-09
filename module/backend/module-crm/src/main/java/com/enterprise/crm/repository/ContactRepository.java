package com.enterprise.crm.repository;

import com.enterprise.crm.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

/**
 * @file ContactRepository.java
 * @description 聯絡人資料存取 / Contact repository
 */
@Repository
public interface ContactRepository extends JpaRepository<Contact, UUID> {
    List<Contact> findByCustomerIdAndDeletedAtIsNullOrderByPrimaryContactDescNameAsc(UUID customerId);
}
