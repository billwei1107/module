package com.enterprise.finance.service.impl;

import com.enterprise.common.annotation.Auditable;
import com.enterprise.common.exception.BusinessException;
import com.enterprise.finance.dto.CreateJournalEntryRequest;
import com.enterprise.finance.dto.JournalEntryDTO;
import com.enterprise.finance.dto.JournalLineDTO;
import com.enterprise.finance.dto.JournalLineRequest;
import com.enterprise.finance.entity.JournalEntry;
import com.enterprise.finance.entity.JournalEntry.EntryStatus;
import com.enterprise.finance.entity.JournalLine;
import com.enterprise.finance.repository.JournalEntryRepository;
import com.enterprise.finance.repository.JournalLineRepository;
import com.enterprise.finance.service.JournalEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * @file JournalEntryServiceImpl.java
 * @description 傳票服務實作 / Journal entry service implementation
 * @description_en Validates debit-credit balance before saving accounting vouchers
 * @description_zh 儲存會計傳票前驗證借貸平衡，避免財務資料不一致
 */
@Service
@RequiredArgsConstructor
public class JournalEntryServiceImpl implements JournalEntryService {

    private final JournalEntryRepository journalEntryRepository;
    private final JournalLineRepository journalLineRepository;

    @Override
    @Transactional
    @Auditable(module = "finance", action = "CREATE_JOURNAL_ENTRY")
    public JournalEntryDTO createJournalEntry(CreateJournalEntryRequest request) {
        if (request.getLines() == null || request.getLines().isEmpty()) {
            throw new BusinessException(400, "傳票分錄不可為空 / Journal lines are required");
        }
        BigDecimal totalDebit = sumDebit(request.getLines());
        BigDecimal totalCredit = sumCredit(request.getLines());
        validateBalanced(totalDebit, totalCredit);

        JournalEntry entry = new JournalEntry();
        entry.setEntryNo(request.getEntryNo());
        entry.setDate(request.getDate() == null ? LocalDate.now() : request.getDate());
        entry.setDescription(request.getDescription());
        entry.setTotalDebit(totalDebit);
        entry.setTotalCredit(totalCredit);
        JournalEntry savedEntry = journalEntryRepository.save(entry);

        List<JournalLine> lines = request.getLines().stream()
                .map(lineRequest -> toLine(savedEntry.getId(), lineRequest))
                .toList();
        journalLineRepository.saveAll(lines);
        return toDTO(savedEntry, lines);
    }

    @Override
    @Transactional
    @Auditable(module = "finance", action = "POST_JOURNAL_ENTRY")
    public JournalEntryDTO postJournalEntry(String id, String postedBy) {
        JournalEntry entry = journalEntryRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new BusinessException(404, "傳票不存在 / Journal entry not found"));
        validateBalanced(entry.getTotalDebit(), entry.getTotalCredit());
        entry.setStatus(EntryStatus.POSTED);
        entry.setPostedBy(postedBy);
        entry.setPostedAt(LocalDateTime.now());
        JournalEntry savedEntry = journalEntryRepository.save(entry);
        return toDTO(savedEntry, journalLineRepository.findByJournalEntryIdAndDeletedAtIsNull(savedEntry.getId()));
    }

    @Override
    public List<JournalEntryDTO> getJournalEntries() {
        return journalEntryRepository.findByDeletedAtIsNullOrderByDateDescEntryNoDesc().stream()
                .map(entry -> toDTO(entry, journalLineRepository.findByJournalEntryIdAndDeletedAtIsNull(entry.getId())))
                .toList();
    }

    private BigDecimal sumDebit(List<JournalLineRequest> lines) {
        return lines.stream()
                .map(line -> safeAmount(line.getDebitAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal sumCredit(List<JournalLineRequest> lines) {
        return lines.stream()
                .map(line -> safeAmount(line.getCreditAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void validateBalanced(BigDecimal totalDebit, BigDecimal totalCredit) {
        if (safeAmount(totalDebit).compareTo(safeAmount(totalCredit)) != 0) {
            throw new BusinessException(400, "借貸不平衡 / Debit and credit totals must be balanced");
        }
    }

    private BigDecimal safeAmount(BigDecimal amount) {
        return amount == null ? BigDecimal.ZERO : amount;
    }

    private JournalLine toLine(UUID entryId, JournalLineRequest request) {
        JournalLine line = new JournalLine();
        line.setJournalEntryId(entryId);
        line.setAccountId(UUID.fromString(request.getAccountId()));
        line.setDebitAmount(safeAmount(request.getDebitAmount()));
        line.setCreditAmount(safeAmount(request.getCreditAmount()));
        line.setDescription(request.getDescription());
        return line;
    }

    private JournalEntryDTO toDTO(JournalEntry entry, List<JournalLine> lines) {
        return JournalEntryDTO.builder()
                .id(entry.getId() != null ? entry.getId().toString() : null)
                .entryNo(entry.getEntryNo())
                .date(entry.getDate())
                .description(entry.getDescription())
                .status(entry.getStatus())
                .totalDebit(entry.getTotalDebit())
                .totalCredit(entry.getTotalCredit())
                .lines(lines.stream().map(this::toLineDTO).toList())
                .build();
    }

    private JournalLineDTO toLineDTO(JournalLine line) {
        return JournalLineDTO.builder()
                .id(line.getId() != null ? line.getId().toString() : null)
                .accountId(line.getAccountId().toString())
                .debitAmount(line.getDebitAmount())
                .creditAmount(line.getCreditAmount())
                .description(line.getDescription())
                .build();
    }
}
