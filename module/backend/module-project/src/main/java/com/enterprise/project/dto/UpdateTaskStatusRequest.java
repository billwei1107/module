package com.enterprise.project.dto;

import com.enterprise.project.entity.Task.TaskStatus;
import lombok.Data;

/**
 * @file UpdateTaskStatusRequest.java
 * @description 更新任務狀態請求 / Update task status request
 */
@Data
public class UpdateTaskStatusRequest {
    private TaskStatus status;
}
