package com.enterprise.project.repository;

import com.enterprise.project.entity.Task;
import com.enterprise.project.entity.Task.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * @file TaskRepository.java
 * @description 任務資料存取 / Task repository
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
    List<Task> findByProjectIdAndDeletedAtIsNullOrderByCreatedAtAsc(UUID projectId);

    List<Task> findByProjectIdAndStatusAndDeletedAtIsNullOrderByCreatedAtAsc(UUID projectId, TaskStatus status);

    List<Task> findByDueDateBeforeAndStatusNotAndDeletedAtIsNull(LocalDate dueDate, TaskStatus status);
}
