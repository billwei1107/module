package com.enterprise.payroll.entity;

import com.enterprise.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * @file TaxConfig.java
 * @description 所得稅級距設定 / Tax bracket configuration
 * @description_en Stores progressive tax bracket rates and deductions
 * @description_zh 儲存累進所得稅級距、稅率與扣除額
 */
@Entity
@Table(name = "pay_tax_configs")
@Data
@EqualsAndHashCode(callSuper = true)
public class TaxConfig extends BaseEntity {

    @Column(name = "bracket_start", nullable = false, precision = 19, scale = 4)
    private BigDecimal bracketStart = BigDecimal.ZERO;

    @Column(name = "bracket_end", precision = 19, scale = 4)
    private BigDecimal bracketEnd;

    @Column(nullable = false, precision = 9, scale = 6)
    private BigDecimal rate = BigDecimal.ZERO;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal deduction = BigDecimal.ZERO;
}
