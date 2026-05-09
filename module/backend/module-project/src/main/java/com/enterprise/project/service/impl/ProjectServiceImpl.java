package com.enterprise.project.service.impl;

import com.enterprise.common.annotation.Auditable;
import com.enterprise.common.event.SystemNotificationEvent;
import com.enterprise.common.exception.BusinessException;
import com.enterprise.project.dto.*;
import com.enterprise.project.entity.*;
import com.enterprise.project.entity.Task.TaskStatus;
import com.enterprise.project.repository.*;
import com.enterprise.project.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @file ProjectServiceImpl.java
 * @description 專案任務服務實作 / Project service implementation
 * @description_en Handles projects, Kanban status changes, Gantt data, time logs, and overdue notifications
 * @description_zh 處理專案、看板狀態、甘特圖資料、工時與逾期通知事件
 */
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final MilestoneRepository milestoneRepository;
    private final TimeLogRepository timeLogRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    @Auditable(module = "project", action = "CREATE_PROJECT")
    public ProjectDTO createProject(CreateProjectRequest request) {
        Project project = new Project();
        project.setName(request.getName());
        project.setOwnerId(request.getOwnerId());
        project.setStartDate(request.getStartDate());
        project.setEndDate(request.getEndDate());
        project.setDescription(request.getDescription());
        project.setStatus(Project.ProjectStatus.ACTIVE);
        return toDTO(projectRepository.save(project));
    }

    @Override
    public List<ProjectDTO> getProjects() {
        return projectRepository.findByDeletedAtIsNullOrderByCreatedAtDesc().stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional
    @Auditable(module = "project", action = "CREATE_TASK")
    public TaskDTO createTask(CreateTaskRequest request) {
        UUID projectId = UUID.fromString(request.getProjectId());
        validateDependencies(projectId, null, request.getDependencyIds());
        Task task = new Task();
        task.setProjectId(projectId);
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setAssigneeId(request.getAssigneeId());
        task.setParentId(parseUuid(request.getParentId()));
        task.setDependencyIds(serializeDependencies(request.getDependencyIds()));
        task.setStartDate(request.getStartDate());
        task.setDueDate(request.getDueDate());
        return toDTO(taskRepository.save(task));
    }

    @Override
    @Transactional
    @Auditable(module = "project", action = "UPDATE_TASK_STATUS")
    public TaskDTO updateTaskStatus(String taskId, UpdateTaskStatusRequest request) {
        Task task = taskRepository.findById(UUID.fromString(taskId))
                .orElseThrow(() -> new BusinessException(404, "任務不存在 / Task not found"));
        if (request.getStatus() == TaskStatus.IN_PROGRESS) {
            ensureDependenciesDone(task);
        }
        task.setStatus(request.getStatus());
        return toDTO(taskRepository.save(task));
    }

    @Override
    public KanbanBoardDTO getKanbanBoard(String projectId) {
        UUID id = UUID.fromString(projectId);
        return KanbanBoardDTO.builder()
                .todo(findTasks(id, TaskStatus.TODO))
                .inProgress(findTasks(id, TaskStatus.IN_PROGRESS))
                .done(findTasks(id, TaskStatus.DONE))
                .build();
    }

    @Override
    public GanttDataDTO getGanttData(String projectId) {
        UUID id = UUID.fromString(projectId);
        return GanttDataDTO.builder()
                .tasks(taskRepository.findByProjectIdAndDeletedAtIsNullOrderByCreatedAtAsc(id).stream().map(this::toDTO).toList())
                .milestones(milestoneRepository.findByProjectIdAndDeletedAtIsNullOrderByDueDateAsc(id).stream().map(this::toDTO).toList())
                .build();
    }

    @Override
    @Transactional
    public MilestoneDTO createMilestone(CreateMilestoneRequest request) {
        Milestone milestone = new Milestone();
        milestone.setProjectId(UUID.fromString(request.getProjectId()));
        milestone.setName(request.getName());
        milestone.setDueDate(request.getDueDate());
        return toDTO(milestoneRepository.save(milestone));
    }

    @Override
    @Transactional
    public void recordTimeLog(RecordTimeLogRequest request) {
        TimeLog log = new TimeLog();
        log.setTaskId(UUID.fromString(request.getTaskId()));
        log.setEmployeeId(request.getEmployeeId());
        log.setMinutes(request.getMinutes() == null ? 0 : request.getMinutes());
        log.setStartedAt(LocalDateTime.now());
        log.setEndedAt(LocalDateTime.now());
        log.setNote(request.getNote());
        timeLogRepository.save(log);
    }

    @Override
    public int publishOverdueTaskNotifications() {
        List<Task> overdueTasks = taskRepository.findByDueDateBeforeAndStatusNotAndDeletedAtIsNull(LocalDate.now(), TaskStatus.DONE);
        overdueTasks.forEach(task -> {
            if (task.getAssigneeId() != null && !task.getAssigneeId().isBlank()) {
                eventPublisher.publishEvent(new SystemNotificationEvent(
                        this,
                        task.getAssigneeId(),
                        "PROJECT_TASK_OVERDUE",
                        "IN_APP",
                        "project",
                        Map.of("taskId", task.getId().toString(), "title", task.getTitle())));
            }
        });
        return overdueTasks.size();
    }

    private List<TaskDTO> findTasks(UUID projectId, TaskStatus status) {
        return taskRepository.findByProjectIdAndStatusAndDeletedAtIsNullOrderByCreatedAtAsc(projectId, status)
                .stream().map(this::toDTO).toList();
    }

    private void validateDependencies(UUID projectId, UUID taskId, List<String> dependencyIds) {
        for (String dependencyId : dependencyIds == null ? List.<String>of() : dependencyIds) {
            UUID id = UUID.fromString(dependencyId);
            if (taskId != null && taskId.equals(id)) {
                throw new BusinessException(400, "任務不可依賴自己 / Task cannot depend on itself");
            }
            Task dependency = taskRepository.findById(id)
                    .orElseThrow(() -> new BusinessException(404, "前置任務不存在 / Dependency task not found"));
            if (!dependency.getProjectId().equals(projectId)) {
                throw new BusinessException(400, "前置任務必須屬於同一專案 / Dependency must belong to the same project");
            }
        }
    }

    private void ensureDependenciesDone(Task task) {
        for (String dependencyId : dependencyList(task.getDependencyIds())) {
            Task dependency = taskRepository.findById(UUID.fromString(dependencyId))
                    .orElseThrow(() -> new BusinessException(404, "前置任務不存在 / Dependency task not found"));
            if (dependency.getStatus() != TaskStatus.DONE) {
                throw new BusinessException(400, "前置任務未完成，不可開始 / Dependency tasks must be done first");
            }
        }
    }

    private String serializeDependencies(List<String> dependencyIds) {
        return dependencyIds == null || dependencyIds.isEmpty() ? "" : String.join(",", dependencyIds);
    }

    private List<String> dependencyList(String dependencyIds) {
        if (dependencyIds == null || dependencyIds.isBlank()) {
            return List.of();
        }
        return Arrays.stream(dependencyIds.split(",")).filter(id -> !id.isBlank()).toList();
    }

    private UUID parseUuid(String value) {
        return value == null || value.isBlank() ? null : UUID.fromString(value);
    }

    private ProjectDTO toDTO(Project project) {
        return ProjectDTO.builder()
                .id(project.getId() != null ? project.getId().toString() : null)
                .name(project.getName())
                .ownerId(project.getOwnerId())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .status(project.getStatus())
                .description(project.getDescription())
                .build();
    }

    private TaskDTO toDTO(Task task) {
        return TaskDTO.builder()
                .id(task.getId() != null ? task.getId().toString() : null)
                .projectId(task.getProjectId().toString())
                .title(task.getTitle())
                .description(task.getDescription())
                .assigneeId(task.getAssigneeId())
                .parentId(task.getParentId() != null ? task.getParentId().toString() : null)
                .dependencyIds(dependencyList(task.getDependencyIds()))
                .status(task.getStatus())
                .startDate(task.getStartDate())
                .dueDate(task.getDueDate())
                .build();
    }

    private MilestoneDTO toDTO(Milestone milestone) {
        return MilestoneDTO.builder()
                .id(milestone.getId() != null ? milestone.getId().toString() : null)
                .projectId(milestone.getProjectId().toString())
                .name(milestone.getName())
                .dueDate(milestone.getDueDate())
                .completed(milestone.getCompleted())
                .build();
    }
}
