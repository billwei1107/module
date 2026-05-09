package com.enterprise.system.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @file SequenceRuleDTO.java
 * @description 流水號規則回傳 / Sequence rule response DTO
 */
@Data
@Builder
public class SequenceRuleDTO {
    private String id;
    private String name;
    private String prefix;
    private String dateFormat;
    private Long currentValue;
    private Integer padLength;
}
