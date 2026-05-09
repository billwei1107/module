package com.enterprise.system.repository;

import com.enterprise.system.entity.DictionaryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * @file DictionaryItemRepository.java
 * @description 資料字典項目資料存取 / Dictionary item repository
 */
@Repository
public interface DictionaryItemRepository extends JpaRepository<DictionaryItem, UUID> {
    List<DictionaryItem> findByDictionaryIdAndDeletedAtIsNullOrderBySortOrderAscLabelAsc(UUID dictionaryId);
}
