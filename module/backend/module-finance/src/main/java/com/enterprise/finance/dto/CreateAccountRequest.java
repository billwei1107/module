package com.enterprise.finance.dto;

import com.enterprise.finance.entity.Account.AccountType;
import lombok.Data;

/**
 * @file CreateAccountRequest.java
 * @description 建立會計科目請求 / Create account request
 */
@Data
public class CreateAccountRequest {
    private String code;
    private String name;
    private String parentId;
    private AccountType type;
    private Integer level;
}
