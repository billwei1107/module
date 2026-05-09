package com.enterprise.finance.dto;

import com.enterprise.finance.entity.Account.AccountType;
import lombok.Builder;
import lombok.Data;

/**
 * @file AccountDTO.java
 * @description 會計科目回傳資料 / Account response DTO
 */
@Data
@Builder
public class AccountDTO {
    private String id;
    private String code;
    private String name;
    private String parentId;
    private AccountType type;
    private Integer level;
    private Boolean active;
}
