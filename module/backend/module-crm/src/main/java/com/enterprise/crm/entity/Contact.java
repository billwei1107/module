package com.enterprise.crm.entity;

import com.enterprise.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.UUID;

/**
 * @file Contact.java
 * @description 客戶聯絡人實體 / Customer contact entity
 * @description_en Stores multiple contacts for a customer
 * @description_zh 儲存客戶的多位聯絡人
 */
@Entity
@Table(name = "crm_contacts")
@Data
@EqualsAndHashCode(callSuper = true)
public class Contact extends BaseEntity {
    @Column(name = "customer_id", nullable = false)
    private UUID customerId;
    @Column(nullable = false, length = 120)
    private String name;
    @Column(length = 120)
    private String title;
    @Column(length = 40)
    private String phone;
    @Column(length = 160)
    private String email;
    @Column(name = "primary_contact", nullable = false)
    private Boolean primaryContact = false;
}
