package com.enterprise.project.repository;

import com.enterprise.project.entity.TimeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * @file TimeLogRepository.java
 * @description 工時資料存取 / Time log repository
 */
@Repository
public interface TimeLogRepository extends JpaRepository<TimeLog, UUID> {
    List<TimeLog> findByTaskIdAndDeletedAtIsNull(UUID taskId);
}
