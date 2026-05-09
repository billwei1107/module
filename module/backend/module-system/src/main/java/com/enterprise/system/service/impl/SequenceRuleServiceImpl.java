package com.enterprise.system.service.impl;

import com.enterprise.common.annotation.Auditable;
import com.enterprise.common.exception.BusinessException;
import com.enterprise.system.entity.SequenceRule;
import com.enterprise.system.repository.SequenceRuleRepository;
import com.enterprise.system.service.SequenceRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @file SequenceRuleServiceImpl.java
 * @description 流水號規則服務實作 / Sequence rule service implementation
 * @description_zh 產生 EMP-2026-0001 類型序號，第一版使用 synchronized 防止單 JVM 內重複
 */
@Service
@RequiredArgsConstructor
public class SequenceRuleServiceImpl implements SequenceRuleService {

    private final SequenceRuleRepository sequenceRuleRepository;

    @Override
    @Transactional
    @Auditable(module = "system", action = "GENERATE_SEQUENCE")
    public synchronized String getNextSequence(String name) {
        SequenceRule rule = sequenceRuleRepository.findByNameAndDeletedAtIsNull(name)
                .orElseThrow(() -> new BusinessException(404, "流水號規則不存在 / Sequence rule not found"));

        long nextValue = rule.getCurrentValue() + 1;
        rule.setCurrentValue(nextValue);
        sequenceRuleRepository.save(rule);

        String datePart = rule.getDateFormat() == null || rule.getDateFormat().isBlank()
                ? ""
                : LocalDate.now().format(DateTimeFormatter.ofPattern(rule.getDateFormat()));
        String numberPart = String.format("%0" + rule.getPadLength() + "d", nextValue);
        return rule.getPrefix() + "-" + datePart + "-" + numberPart;
    }
}
