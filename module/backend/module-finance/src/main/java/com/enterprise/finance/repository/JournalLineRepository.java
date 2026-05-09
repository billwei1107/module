package com.enterprise.finance.repository;

import com.enterprise.finance.entity.JournalLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * @file JournalLineRepository.java
 * @description 傳票分錄資料存取 / Journal line repository
 */
@Repository
public interface JournalLineRepository extends JpaRepository<JournalLine, UUID> {
    List<JournalLine> findByJournalEntryIdAndDeletedAtIsNull(UUID journalEntryId);
}
