package com.enterprise.project.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

/**
 * @file MilestoneDTO.java
 * @description 里程碑回傳資料 / Milestone response DTO
 */
@Data
@Builder
public class MilestoneDTO {
    private String id;
    private String projectId;
    private String name;
    private LocalDate dueDate;
    private Boolean completed;
}
