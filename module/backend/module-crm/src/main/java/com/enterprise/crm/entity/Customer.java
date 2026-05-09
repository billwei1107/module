package com.enterprise.crm.entity;

import com.enterprise.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @file Customer.java
 * @description 客戶實體 / Customer entity
 * @description_en Stores company and individual customer profile data
 * @description_zh 儲存公司與個人客戶主檔資料
 */
@Entity
@Table(name = "crm_customers")
@Data
@EqualsAndHashCode(callSuper = true)
public class Customer extends BaseEntity {
    public enum CustomerType { COMPANY, INDIVIDUAL }
    public enum CustomerGrade { VIP, REGULAR, PROSPECT }

    @Column(nullable = false, length = 180)
    private String name;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CustomerType type = CustomerType.COMPANY;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CustomerGrade grade = CustomerGrade.PROSPECT;
    @Column(name = "owner_id", length = 80)
    private String ownerId;
    @Column(length = 40)
    private String phone;
    @Column(length = 160)
    private String email;
    @Column(columnDefinition = "TEXT")
    private String address;
}
