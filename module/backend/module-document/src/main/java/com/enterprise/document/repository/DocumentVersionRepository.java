package com.enterprise.document.repository;

import com.enterprise.document.entity.DocumentVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * @file DocumentVersionRepository.java
 * @description 文件版本資料存取 / Document version repository
 */
@Repository
public interface DocumentVersionRepository extends JpaRepository<DocumentVersion, UUID> {
    List<DocumentVersion> findByDocumentIdAndDeletedAtIsNullOrderByVersionDesc(UUID documentId);
}
