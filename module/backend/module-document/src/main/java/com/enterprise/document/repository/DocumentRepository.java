package com.enterprise.document.repository;

import com.enterprise.document.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * @file DocumentRepository.java
 * @description 文件資料存取 / Document repository
 */
@Repository
public interface DocumentRepository extends JpaRepository<Document, UUID> {
    List<Document> findByFolderIdAndDeletedAtIsNullOrderByCreatedAtDesc(UUID folderId);

    List<Document> findByDeletedAtIsNullOrderByCreatedAtDesc();
}
