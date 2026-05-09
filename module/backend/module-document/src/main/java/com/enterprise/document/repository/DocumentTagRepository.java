package com.enterprise.document.repository;

import com.enterprise.document.entity.DocumentTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @file DocumentTagRepository.java
 * @description 文件標籤資料存取 / Document tag repository
 */
@Repository
public interface DocumentTagRepository extends JpaRepository<DocumentTag, UUID> {
    List<DocumentTag> findByDocumentIdAndDeletedAtIsNullOrderByNameAsc(UUID documentId);

    Optional<DocumentTag> findFirstByDocumentIdAndNameAndDeletedAtIsNull(UUID documentId, String name);
}
