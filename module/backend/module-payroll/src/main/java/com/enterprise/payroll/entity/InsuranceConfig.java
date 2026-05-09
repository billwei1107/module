package com.enterprise.payroll.entity;

import com.enterprise.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * @file InsuranceConfig.java
 * @description 勞健保設定 / Insurance configuration
 * @description_en Stores employee and employer rates with salary ceiling
 * @description_zh 儲存勞健保員工與雇主費率及投保上限
 */
@Entity
@Table(name = "pay_insurance_configs")
@Data
@EqualsAndHashCode(callSuper = true)
public class InsuranceConfig extends BaseEntity {

    public enum InsuranceType {
        LABOR, HEALTH
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 20)
    private InsuranceType type;

    @Column(name = "employee_rate", nullable = false, precision = 9, scale = 6)
    private BigDecimal employeeRate = BigDecimal.ZERO;

    @Column(name = "employer_rate", nullable = false, precision = 9, scale = 6)
    private BigDecimal employerRate = BigDecimal.ZERO;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal ceiling = BigDecimal.ZERO;
}
