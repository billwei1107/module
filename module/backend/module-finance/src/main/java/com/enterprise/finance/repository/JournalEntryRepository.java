package com.enterprise.finance.repository;

import com.enterprise.finance.entity.JournalEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @file JournalEntryRepository.java
 * @description 傳票主檔資料存取 / Journal entry repository
 */
@Repository
public interface JournalEntryRepository extends JpaRepository<JournalEntry, UUID> {
    Optional<JournalEntry> findByEntryNoAndDeletedAtIsNull(String entryNo);

    List<JournalEntry> findByDeletedAtIsNullOrderByDateDescEntryNoDesc();
}
