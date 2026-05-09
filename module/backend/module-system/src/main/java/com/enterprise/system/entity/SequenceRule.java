package com.enterprise.system.entity;

import com.enterprise.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @file SequenceRule.java
 * @description 流水號規則實體 / Sequence rule entity
 * @description_zh 定義員工編號、單據編號等序號產生規則
 */
@Entity
@Table(name = "sys_sequence_rules")
@Data
@EqualsAndHashCode(callSuper = true)
public class SequenceRule extends BaseEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(nullable = false, length = 30)
    private String prefix;

    @Column(name = "date_format", length = 20)
    private String dateFormat = "yyyy";

    @Column(name = "current_value", nullable = false)
    private Long currentValue = 0L;

    @Column(name = "pad_length", nullable = false)
    private Integer padLength = 4;
}
