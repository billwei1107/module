package com.enterprise.project.service.impl;

import com.enterprise.common.exception.BusinessException;
import com.enterprise.project.dto.UpdateTaskStatusRequest;
import com.enterprise.project.entity.Milestone;
import com.enterprise.project.entity.Task;
import com.enterprise.project.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @file ProjectServiceImplTest.java
 * @description 專案任務服務測試 / Project service tests
 */
class ProjectServiceImplTest {

    @Test
    void updateTaskStatusShouldRejectWhenDependencyIsNotDone() {
        TaskRepository taskRepository = mock(TaskRepository.class);
        UUID projectId = UUID.randomUUID();
        UUID dependencyId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        Task dependency = task(projectId, dependencyId, Task.TaskStatus.TODO, "");
        Task task = task(projectId, taskId, Task.TaskStatus.TODO, dependencyId.toString());
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.findById(dependencyId)).thenReturn(Optional.of(dependency));
        ProjectServiceImpl service = service(taskRepository, mock(MilestoneRepository.class));

        UpdateTaskStatusRequest request = new UpdateTaskStatusRequest();
        request.setStatus(Task.TaskStatus.IN_PROGRESS);

        assertThatThrownBy(() -> service.updateTaskStatus(taskId.toString(), request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("前置任務未完成");
    }

    @Test
    void getGanttDataShouldReturnTasksAndMilestones() {
        TaskRepository taskRepository = mock(TaskRepository.class);
        MilestoneRepository milestoneRepository = mock(MilestoneRepository.class);
        UUID projectId = UUID.randomUUID();
        Task task = task(projectId, UUID.randomUUID(), Task.TaskStatus.TODO, "");
        task.setTitle("設計資料表");
        Milestone milestone = new Milestone();
        milestone.setId(UUID.randomUUID());
        milestone.setProjectId(projectId);
        milestone.setName("MVP");
        milestone.setDueDate(LocalDate.of(2026, 6, 1));
        milestone.setCompleted(false);
        when(taskRepository.findByProjectIdAndDeletedAtIsNullOrderByCreatedAtAsc(projectId)).thenReturn(List.of(task));
        when(milestoneRepository.findByProjectIdAndDeletedAtIsNullOrderByDueDateAsc(projectId)).thenReturn(List.of(milestone));

        ProjectServiceImpl service = service(taskRepository, milestoneRepository);

        assertThat(service.getGanttData(projectId.toString()).getTasks()).hasSize(1);
        assertThat(service.getGanttData(projectId.toString()).getMilestones()).hasSize(1);
    }

    private ProjectServiceImpl service(TaskRepository taskRepository, MilestoneRepository milestoneRepository) {
        return new ProjectServiceImpl(
                mock(ProjectRepository.class),
                taskRepository,
                milestoneRepository,
                mock(TimeLogRepository.class),
                mock(ApplicationEventPublisher.class));
    }

    private Task task(UUID projectId, UUID id, Task.TaskStatus status, String dependencies) {
        Task task = new Task();
        task.setId(id);
        task.setProjectId(projectId);
        task.setTitle("任務");
        task.setStatus(status);
        task.setDependencyIds(dependencies);
        return task;
    }
}
