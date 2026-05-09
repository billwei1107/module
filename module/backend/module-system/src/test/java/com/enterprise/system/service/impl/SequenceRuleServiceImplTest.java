package com.enterprise.system.service.impl;

import com.enterprise.system.entity.SequenceRule;
import com.enterprise.system.repository.SequenceRuleRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @file SequenceRuleServiceImplTest.java
 * @description 流水號服務測試 / Sequence rule service tests
 */
class SequenceRuleServiceImplTest {

    @Test
    void getNextSequenceShouldIncreaseCurrentValueAndFormatCode() {
        SequenceRuleRepository repository = mock(SequenceRuleRepository.class);
        SequenceRule rule = new SequenceRule();
        rule.setName("EMP");
        rule.setPrefix("EMP");
        rule.setDateFormat("yyyy");
        rule.setCurrentValue(0L);
        rule.setPadLength(4);

        when(repository.findByNameAndDeletedAtIsNull("EMP")).thenReturn(Optional.of(rule));
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        SequenceRuleServiceImpl service = new SequenceRuleServiceImpl(repository);
        String sequence = service.getNextSequence("EMP");

        assertThat(sequence).startsWith("EMP-");
        assertThat(sequence).endsWith("-0001");
        assertThat(rule.getCurrentValue()).isEqualTo(1L);
    }
}
