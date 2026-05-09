package com.enterprise.crm.repository;

import com.enterprise.crm.entity.Opportunity;
import com.enterprise.crm.entity.Opportunity.Stage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

/**
 * @file OpportunityRepository.java
 * @description 商機資料存取 / Opportunity repository
 */
@Repository
public interface OpportunityRepository extends JpaRepository<Opportunity, UUID> {
    List<Opportunity> findByDeletedAtIsNullOrderByCreatedAtDesc();
    long countByStageAndDeletedAtIsNull(Stage stage);
}
