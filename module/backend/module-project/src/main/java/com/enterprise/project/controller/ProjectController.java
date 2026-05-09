package com.enterprise.project.controller;

import com.enterprise.common.dto.ApiResponse;
import com.enterprise.project.dto.*;
import com.enterprise.project.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @file ProjectController.java
 * @description 專案任務控制器 / Project task controller
 */
@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    public ApiResponse<List<ProjectDTO>> getProjects() {
        return ApiResponse.success(projectService.getProjects());
    }

    @PostMapping
    public ApiResponse<ProjectDTO> createProject(@RequestBody CreateProjectRequest request) {
        return ApiResponse.success(projectService.createProject(request));
    }

    @PostMapping("/tasks")
    public ApiResponse<TaskDTO> createTask(@RequestBody CreateTaskRequest request) {
        return ApiResponse.success(projectService.createTask(request));
    }

    @PostMapping("/tasks/{id}/status")
    public ApiResponse<TaskDTO> updateTaskStatus(@PathVariable String id, @RequestBody UpdateTaskStatusRequest request) {
        return ApiResponse.success(projectService.updateTaskStatus(id, request));
    }

    @GetMapping("/{projectId}/kanban")
    public ApiResponse<KanbanBoardDTO> getKanbanBoard(@PathVariable String projectId) {
        return ApiResponse.success(projectService.getKanbanBoard(projectId));
    }

    @GetMapping("/{projectId}/gantt")
    public ApiResponse<GanttDataDTO> getGanttData(@PathVariable String projectId) {
        return ApiResponse.success(projectService.getGanttData(projectId));
    }

    @PostMapping("/milestones")
    public ApiResponse<MilestoneDTO> createMilestone(@RequestBody CreateMilestoneRequest request) {
        return ApiResponse.success(projectService.createMilestone(request));
    }

    @PostMapping("/time-logs")
    public ApiResponse<Void> recordTimeLog(@RequestBody RecordTimeLogRequest request) {
        projectService.recordTimeLog(request);
        return ApiResponse.success();
    }

    @PostMapping("/overdue/check")
    public ApiResponse<Integer> publishOverdueTaskNotifications() {
        return ApiResponse.success(projectService.publishOverdueTaskNotifications());
    }
}
