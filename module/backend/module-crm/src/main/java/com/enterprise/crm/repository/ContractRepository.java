package com.enterprise.crm.repository;

import com.enterprise.crm.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * @file ContractRepository.java
 * @description 合約資料存取 / Contract repository
 */
@Repository
public interface ContractRepository extends JpaRepository<Contract, UUID> {
    List<Contract> findByDeletedAtIsNullOrderByEndDateAsc();
    List<Contract> findByEndDateBetweenAndDeletedAtIsNullOrderByEndDateAsc(LocalDate startDate, LocalDate endDate);
}
