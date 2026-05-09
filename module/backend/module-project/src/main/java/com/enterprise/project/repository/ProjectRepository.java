package com.enterprise.project.repository;

import com.enterprise.project.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * @file ProjectRepository.java
 * @description 專案資料存取 / Project repository
 */
@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {
    List<Project> findByDeletedAtIsNullOrderByCreatedAtDesc();
}
