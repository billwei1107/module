package com.enterprise.finance.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @file CreateJournalEntryRequest.java
 * @description 建立傳票請求 / Create journal entry request
 */
@Data
public class CreateJournalEntryRequest {
    private String entryNo;
    private LocalDate date;
    private String description;
    private List<JournalLineRequest> lines = new ArrayList<>();
}
