package com.enterprise.finance.entity;

import com.enterprise.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * @file JournalLine.java
 * @description 傳票分錄實體 / Journal line entity
 * @description_en Stores debit and credit amounts for a journal entry line
 * @description_zh 儲存傳票單筆分錄的借方與貸方金額
 */
@Entity
@Table(name = "fin_journal_lines")
@Data
@EqualsAndHashCode(callSuper = true)
public class JournalLine extends BaseEntity {

    @Column(name = "journal_entry_id", nullable = false)
    private UUID journalEntryId;

    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    @Column(name = "debit_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal debitAmount = BigDecimal.ZERO;

    @Column(name = "credit_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal creditAmount = BigDecimal.ZERO;

    @Column(columnDefinition = "TEXT")
    private String description;
}
