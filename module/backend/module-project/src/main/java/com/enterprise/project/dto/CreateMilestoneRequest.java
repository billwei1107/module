package com.enterprise.project.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * @file CreateMilestoneRequest.java
 * @description 建立里程碑請求 / Create milestone request
 */
@Data
public class CreateMilestoneRequest {
    private String projectId;
    private String name;
    private LocalDate dueDate;
}
