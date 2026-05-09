package com.enterprise.finance.service;

import com.enterprise.finance.dto.CreateJournalEntryRequest;
import com.enterprise.finance.dto.JournalEntryDTO;

import java.util.List;

/**
 * @file JournalEntryService.java
 * @description 傳票服務介面 / Journal entry service contract
 */
public interface JournalEntryService {
    JournalEntryDTO createJournalEntry(CreateJournalEntryRequest request);

    JournalEntryDTO postJournalEntry(String id, String postedBy);

    List<JournalEntryDTO> getJournalEntries();
}
