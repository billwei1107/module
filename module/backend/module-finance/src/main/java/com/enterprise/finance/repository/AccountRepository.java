package com.enterprise.finance.repository;

import com.enterprise.finance.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @file AccountRepository.java
 * @description 會計科目資料存取 / Account repository
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
    Optional<Account> findByCodeAndDeletedAtIsNull(String code);

    List<Account> findByDeletedAtIsNullOrderByCodeAsc();
}
