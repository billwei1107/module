package com.enterprise.finance.service.impl;

import com.enterprise.common.exception.BusinessException;
import com.enterprise.finance.dto.AccountDTO;
import com.enterprise.finance.dto.CreateAccountRequest;
import com.enterprise.finance.entity.Account;
import com.enterprise.finance.repository.AccountRepository;
import com.enterprise.finance.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * @file AccountServiceImpl.java
 * @description 會計科目服務實作 / Account service implementation
 */
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public AccountDTO createAccount(CreateAccountRequest request) {
        if (request.getCode() == null || request.getCode().isBlank()) {
            throw new BusinessException(400, "科目代碼不可為空 / Account code is required");
        }
        if (accountRepository.findByCodeAndDeletedAtIsNull(request.getCode()).isPresent()) {
            throw new BusinessException(409, "科目代碼已存在 / Account code already exists");
        }
        Account account = new Account();
        account.setCode(request.getCode());
        account.setName(request.getName());
        account.setType(request.getType());
        account.setLevel(request.getLevel() == null ? 1 : request.getLevel());
        if (request.getParentId() != null && !request.getParentId().isBlank()) {
            account.setParentId(UUID.fromString(request.getParentId()));
        }
        return toDTO(accountRepository.save(account));
    }

    @Override
    public List<AccountDTO> getAccounts() {
        return accountRepository.findByDeletedAtIsNullOrderByCodeAsc().stream().map(this::toDTO).toList();
    }

    private AccountDTO toDTO(Account account) {
        return AccountDTO.builder()
                .id(account.getId() != null ? account.getId().toString() : null)
                .code(account.getCode())
                .name(account.getName())
                .parentId(account.getParentId() != null ? account.getParentId().toString() : null)
                .type(account.getType())
                .level(account.getLevel())
                .active(account.getActive())
                .build();
    }
}
