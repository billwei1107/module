package com.enterprise.project.entity;

import com.enterprise.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

/**
 * @file ProjectMember.java
 * @description 專案成員實體 / Project member entity
 * @description_en Stores employees assigned to a project
 * @description_zh 儲存專案成員與角色
 */
@Entity
@Table(name = "proj_members")
@Data
@EqualsAndHashCode(callSuper = true)
public class ProjectMember extends BaseEntity {

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "employee_id", nullable = false, length = 80)
    private String employeeId;

    @Column(nullable = false, length = 60)
    private String role = "MEMBER";
}
