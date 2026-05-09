package com.enterprise.project.dto;

import com.enterprise.project.entity.Task.TaskStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @file TaskDTO.java
 * @description 任務回傳資料 / Task response DTO
 */
@Data
@Builder
public class TaskDTO {
    private String id;
    private String projectId;
    private String title;
    private String description;
    private String assigneeId;
    private String parentId;
    private List<String> dependencyIds;
    private TaskStatus status;
    private LocalDate startDate;
    private LocalDate dueDate;
}
