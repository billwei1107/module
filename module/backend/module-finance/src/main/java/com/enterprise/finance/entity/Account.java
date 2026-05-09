package com.enterprise.finance.entity;

import com.enterprise.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

/**
 * @file Account.java
 * @description 會計科目實體 / Account entity
 * @description_en Stores chart of accounts with optional parent-child hierarchy
 * @description_zh 儲存可樹狀管理的會計科目
 */
@Entity
@Table(name = "fin_accounts")
@Data
@EqualsAndHashCode(callSuper = true)
public class Account extends BaseEntity {

    public enum AccountType {
        ASSET, LIABILITY, EQUITY, REVENUE, EXPENSE
    }

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(name = "parent_id")
    private UUID parentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AccountType type;

    @Column(nullable = false)
    private Integer level = 1;

    @Column(nullable = false)
    private Boolean active = true;
}
