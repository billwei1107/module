package com.enterprise.finance.controller;

import com.enterprise.common.dto.ApiResponse;
import com.enterprise.finance.dto.AccountDTO;
import com.enterprise.finance.dto.CreateAccountRequest;
import com.enterprise.finance.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @file AccountController.java
 * @description 會計科目控制器 / Account controller
 */
@RestController
@RequestMapping("/api/v1/finance/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    public ApiResponse<List<AccountDTO>> getAccounts() {
        return ApiResponse.success(accountService.getAccounts());
    }

    @PostMapping
    public ApiResponse<AccountDTO> createAccount(@RequestBody CreateAccountRequest request) {
        return ApiResponse.success(accountService.createAccount(request));
    }
}
