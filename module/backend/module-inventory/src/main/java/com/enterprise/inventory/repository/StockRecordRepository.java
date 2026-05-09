package com.enterprise.inventory.repository;

import com.enterprise.inventory.entity.StockRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @file StockRecordRepository.java
 * @description 庫存紀錄資料存取 / Stock record repository
 */
@Repository
public interface StockRecordRepository extends JpaRepository<StockRecord, UUID> {
    Optional<StockRecord> findByItemIdAndWarehouseIdAndDeletedAtIsNull(UUID itemId, UUID warehouseId);
    List<StockRecord> findByDeletedAtIsNullOrderByCreatedAtDesc();
    List<StockRecord> findByItemIdAndDeletedAtIsNull(UUID itemId);
}
