package com.enterprise.system.service;

/**
 * @file SequenceRuleService.java
 * @description 流水號規則服務介面 / Sequence rule service interface
 */
public interface SequenceRuleService {
    String getNextSequence(String name);
}
