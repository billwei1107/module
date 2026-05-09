package com.enterprise.inventory.repository;

import com.enterprise.inventory.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @file ItemRepository.java
 * @description 品項資料存取 / Item repository
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, UUID> {
    Optional<Item> findBySkuAndDeletedAtIsNull(String sku);
    List<Item> findByDeletedAtIsNullOrderByNameAsc();
}
