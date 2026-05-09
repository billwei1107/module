package com.enterprise.project.repository;

import com.enterprise.project.entity.Milestone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * @file MilestoneRepository.java
 * @description 里程碑資料存取 / Milestone repository
 */
@Repository
public interface MilestoneRepository extends JpaRepository<Milestone, UUID> {
    List<Milestone> findByProjectIdAndDeletedAtIsNullOrderByDueDateAsc(UUID projectId);
}
