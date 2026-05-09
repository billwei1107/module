package com.enterprise.finance.entity;

import com.enterprise.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @file JournalEntry.java
 * @description 傳票主檔實體 / Journal entry entity
 * @description_en Stores balanced accounting vouchers before and after posting
 * @description_zh 儲存過帳前後的借貸平衡會計傳票
 */
@Entity
@Table(name = "fin_journal_entries")
@Data
@EqualsAndHashCode(callSuper = true)
public class JournalEntry extends BaseEntity {

    public enum EntryStatus {
        DRAFT, POSTED, VOIDED
    }

    @Column(name = "entry_no", nullable = false, unique = true, length = 50)
    private String entryNo;

    @Column(name = "entry_date", nullable = false)
    private LocalDate date;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EntryStatus status = EntryStatus.DRAFT;

    @Column(name = "total_debit", nullable = false, precision = 19, scale = 4)
    private BigDecimal totalDebit = BigDecimal.ZERO;

    @Column(name = "total_credit", nullable = false, precision = 19, scale = 4)
    private BigDecimal totalCredit = BigDecimal.ZERO;

    @Column(name = "posted_by", length = 80)
    private String postedBy;

    @Column(name = "posted_at")
    private LocalDateTime postedAt;
}
