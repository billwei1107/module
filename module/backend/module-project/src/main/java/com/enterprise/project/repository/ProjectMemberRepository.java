package com.enterprise.project.repository;

import com.enterprise.project.entity.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * @file ProjectMemberRepository.java
 * @description 專案成員資料存取 / Project member repository
 */
@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, UUID> {
    List<ProjectMember> findByProjectIdAndDeletedAtIsNull(UUID projectId);
}
