package com.enterprise.project.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * @file CreateProjectRequest.java
 * @description 建立專案請求 / Create project request
 */
@Data
public class CreateProjectRequest {
    private String name;
    private String ownerId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
}
