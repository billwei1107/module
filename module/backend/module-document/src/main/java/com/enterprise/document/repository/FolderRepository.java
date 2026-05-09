package com.enterprise.document.repository;

import com.enterprise.document.entity.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * @file FolderRepository.java
 * @description 文件資料夾資料存取 / Folder repository
 */
@Repository
public interface FolderRepository extends JpaRepository<Folder, UUID> {
    List<Folder> findByParentIdAndDeletedAtIsNullOrderByNameAsc(UUID parentId);

    List<Folder> findByDeletedAtIsNullOrderByPathAsc();
}
