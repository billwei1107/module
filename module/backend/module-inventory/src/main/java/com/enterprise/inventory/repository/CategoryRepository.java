package com.enterprise.inventory.repository;

import com.enterprise.inventory.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

/**
 * @file CategoryRepository.java
 * @description 品項分類資料存取 / Category repository
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    List<Category> findByDeletedAtIsNullOrderByNameAsc();
}
