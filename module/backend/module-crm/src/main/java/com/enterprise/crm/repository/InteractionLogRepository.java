package com.enterprise.crm.repository;

import com.enterprise.crm.entity.InteractionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * @file InteractionLogRepository.java
 * @description 互動紀錄資料存取 / Interaction log repository
 */
@Repository
public interface InteractionLogRepository extends JpaRepository<InteractionLog, UUID> {
    List<InteractionLog> findByCustomerIdAndDeletedAtIsNullOrderByCreatedAtDesc(UUID customerId);
    List<InteractionLog> findByNextFollowUpAtBeforeAndCompletedFalseAndDeletedAtIsNullOrderByNextFollowUpAtAsc(LocalDateTime before);
}
