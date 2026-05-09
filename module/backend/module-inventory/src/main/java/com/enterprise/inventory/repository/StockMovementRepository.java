package com.enterprise.inventory.repository;

import com.enterprise.inventory.entity.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

/**
 * @file StockMovementRepository.java
 * @description 庫存異動資料存取 / Stock movement repository
 */
@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, UUID> {
    List<StockMovement> findByDeletedAtIsNullOrderByCreatedAtDesc();
}
