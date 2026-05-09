package com.enterprise.finance.dto;

import com.enterprise.finance.entity.JournalEntry.EntryStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * @file JournalEntryDTO.java
 * @description 傳票回傳資料 / Journal entry response DTO
 */
@Data
@Builder
public class JournalEntryDTO {
    private String id;
    private String entryNo;
    private LocalDate date;
    private String description;
    private EntryStatus status;
    private BigDecimal totalDebit;
    private BigDecimal totalCredit;
    private List<JournalLineDTO> lines;
}
