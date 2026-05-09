package com.enterprise.finance.service;

import com.enterprise.finance.dto.AccountDTO;
import com.enterprise.finance.dto.CreateAccountRequest;

import java.util.List;

/**
 * @file AccountService.java
 * @description 會計科目服務介面 / Account service contract
 */
public interface AccountService {
    AccountDTO createAccount(CreateAccountRequest request);

    List<AccountDTO> getAccounts();
}
