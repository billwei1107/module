package com.enterprise.system.repository;

import com.enterprise.system.entity.SequenceRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * @file SequenceRuleRepository.java
 * @description 流水號規則資料存取 / Sequence rule repository
 */
@Repository
public interface SequenceRuleRepository extends JpaRepository<SequenceRule, UUID> {
    Optional<SequenceRule> findByNameAndDeletedAtIsNull(String name);
}
