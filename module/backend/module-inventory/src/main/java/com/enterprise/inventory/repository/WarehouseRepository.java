package com.enterprise.inventory.repository;

import com.enterprise.inventory.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

/**
 * @file WarehouseRepository.java
 * @description 倉庫資料存取 / Warehouse repository
 */
@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, UUID> {
    List<Warehouse> findByDeletedAtIsNullOrderByCodeAsc();
}
