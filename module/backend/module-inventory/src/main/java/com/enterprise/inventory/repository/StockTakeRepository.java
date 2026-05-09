package com.enterprise.inventory.repository;

import com.enterprise.inventory.entity.StockTake;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

/**
 * @file StockTakeRepository.java
 * @description 盤點資料存取 / Stock take repository
 */
@Repository
public interface StockTakeRepository extends JpaRepository<StockTake, UUID> {
    List<StockTake> findByDeletedAtIsNullOrderByCreatedAtDesc();
}
