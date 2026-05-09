package com.enterprise.finance.service.impl;

import com.enterprise.common.exception.BusinessException;
import com.enterprise.finance.dto.CreateJournalEntryRequest;
import com.enterprise.finance.dto.JournalLineRequest;
import com.enterprise.finance.entity.JournalEntry;
import com.enterprise.finance.repository.JournalEntryRepository;
import com.enterprise.finance.repository.JournalLineRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @file JournalEntryServiceImplTest.java
 * @description 傳票服務測試 / Journal entry service tests
 */
class JournalEntryServiceImplTest {

    @Test
    void createJournalEntryShouldRejectUnbalancedLines() {
        JournalEntryServiceImpl service = new JournalEntryServiceImpl(
                mock(JournalEntryRepository.class), mock(JournalLineRepository.class));

        CreateJournalEntryRequest request = new CreateJournalEntryRequest();
        request.getLines().add(line("1000.00", "0.00"));
        request.getLines().add(line("0.00", "999.00"));

        assertThatThrownBy(() -> service.createJournalEntry(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("借貸不平衡");
    }

    @Test
    void createJournalEntryShouldSaveBalancedLinesWithBigDecimalPrecision() {
        JournalEntryRepository entryRepository = mock(JournalEntryRepository.class);
        JournalLineRepository lineRepository = mock(JournalLineRepository.class);
        when(entryRepository.save(any())).thenAnswer(invocation -> {
            JournalEntry entry = invocation.getArgument(0);
            entry.setId(UUID.randomUUID());
            return entry;
        });
        when(lineRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

        JournalEntryServiceImpl service = new JournalEntryServiceImpl(entryRepository, lineRepository);
        CreateJournalEntryRequest request = new CreateJournalEntryRequest();
        request.setEntryNo("JE-001");
        request.getLines().add(line("0.10", "0.00"));
        request.getLines().add(line("0.20", "0.00"));
        request.getLines().add(line("0.00", "0.30"));

        assertThat(service.createJournalEntry(request).getTotalDebit()).isEqualByComparingTo(new BigDecimal("0.30"));
    }

    private JournalLineRequest line(String debit, String credit) {
        JournalLineRequest line = new JournalLineRequest();
        line.setAccountId(UUID.randomUUID().toString());
        line.setDebitAmount(new BigDecimal(debit));
        line.setCreditAmount(new BigDecimal(credit));
        return line;
    }
}
