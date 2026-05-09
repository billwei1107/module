package com.enterprise.project.dto;

import com.enterprise.project.entity.Project.ProjectStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

/**
 * @file ProjectDTO.java
 * @description 專案回傳資料 / Project response DTO
 */
@Data
@Builder
public class ProjectDTO {
    private String id;
    private String name;
    private String ownerId;
    private LocalDate startDate;
    private LocalDate endDate;
    private ProjectStatus status;
    private String description;
}
