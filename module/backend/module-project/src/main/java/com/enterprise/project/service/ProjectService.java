package com.enterprise.project.service;

import com.enterprise.project.dto.*;

import java.util.List;

/**
 * @file ProjectService.java
 * @description 專案任務服務介面 / Project service contract
 */
public interface ProjectService {
    ProjectDTO createProject(CreateProjectRequest request);

    List<ProjectDTO> getProjects();

    TaskDTO createTask(CreateTaskRequest request);

    TaskDTO updateTaskStatus(String taskId, UpdateTaskStatusRequest request);

    KanbanBoardDTO getKanbanBoard(String projectId);

    GanttDataDTO getGanttData(String projectId);

    MilestoneDTO createMilestone(CreateMilestoneRequest request);

    void recordTimeLog(RecordTimeLogRequest request);

    int publishOverdueTaskNotifications();
}
