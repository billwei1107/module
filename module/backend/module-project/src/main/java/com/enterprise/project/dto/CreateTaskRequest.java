package com.enterprise.project.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @file CreateTaskRequest.java
 * @description 建立任務請求 / Create task request
 */
@Data
public class CreateTaskRequest {
    private String projectId;
    private String title;
    private String description;
    private String assigneeId;
    private String parentId;
    private List<String> dependencyIds = new ArrayList<>();
    private LocalDate startDate;
    private LocalDate dueDate;
}
