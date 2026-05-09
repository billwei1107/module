package com.enterprise.crm.repository;

import com.enterprise.crm.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

/**
 * @file CustomerRepository.java
 * @description 客戶資料存取 / Customer repository
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    List<Customer> findByDeletedAtIsNullOrderByCreatedAtDesc();
}
