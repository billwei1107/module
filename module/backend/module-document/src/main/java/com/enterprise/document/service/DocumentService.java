package com.enterprise.document.service;

import com.enterprise.document.dto.*;
import com.enterprise.document.entity.DocumentShare.Permission;

import java.util.List;

/**
 * @file DocumentService.java
 * @description 文件管理服務介面 / Document service contract
 */
public interface DocumentService {
    FolderDTO createFolder(CreateFolderRequest request);

    List<FolderDTO> getFolders(String parentId);

    FolderDTO updateFolder(String folderId, UpdateFolderRequest request);

    void deleteFolder(String folderId);

    DocumentDTO registerDocument(RegisterDocumentRequest request);

    List<DocumentDTO> getDocuments(String folderId);

    DocumentDTO getDocument(String documentId);

    DocumentVersionDTO registerDocumentVersion(String documentId, RegisterDocumentVersionRequest request);

    List<DocumentVersionDTO> getVersions(String documentId);

    DocumentShareDTO shareDocument(String documentId, ShareDocumentRequest request);

    List<DocumentShareDTO> getShares(String documentId);

    DocumentAccessDTO checkAccess(String documentId, String userId, Permission requiredPermission);

    DocumentTagDTO assignTag(String documentId, AssignTagRequest request);

    List<DocumentTagDTO> getTags(String documentId);
}
