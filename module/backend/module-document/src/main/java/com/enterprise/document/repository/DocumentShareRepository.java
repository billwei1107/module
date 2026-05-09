package com.enterprise.document.repository;

import com.enterprise.document.entity.DocumentShare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @file DocumentShareRepository.java
 * @description 文件分享資料存取 / Document share repository
 */
@Repository
public interface DocumentShareRepository extends JpaRepository<DocumentShare, UUID> {
    List<DocumentShare> findByDocumentIdAndDeletedAtIsNullOrderByCreatedAtDesc(UUID documentId);

    Optional<DocumentShare> findFirstByDocumentIdAndSharedWithAndDeletedAtIsNull(UUID documentId, String sharedWith);
}
