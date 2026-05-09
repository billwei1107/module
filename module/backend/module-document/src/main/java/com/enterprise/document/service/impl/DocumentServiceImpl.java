package com.enterprise.document.service.impl;

import com.enterprise.common.annotation.Auditable;
import com.enterprise.common.exception.BusinessException;
import com.enterprise.document.dto.*;
import com.enterprise.document.entity.Document;
import com.enterprise.document.entity.DocumentShare;
import com.enterprise.document.entity.DocumentShare.Permission;
import com.enterprise.document.entity.DocumentTag;
import com.enterprise.document.entity.DocumentVersion;
import com.enterprise.document.entity.Folder;
import com.enterprise.document.repository.*;
import com.enterprise.document.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * @file DocumentServiceImpl.java
 * @description 文件管理服務實作 / Document service implementation
 * @description_en Handles folder metadata, document metadata, immutable versions, shares, and tags
 * @description_zh 處理資料夾中繼資料、文件中繼資料、不可變版本、分享權限與標籤
 */
@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final FolderRepository folderRepository;
    private final DocumentRepository documentRepository;
    private final DocumentVersionRepository versionRepository;
    private final DocumentShareRepository shareRepository;
    private final DocumentTagRepository tagRepository;

    @Override
    @Transactional
    @Auditable(module = "document", action = "CREATE_FOLDER")
    public FolderDTO createFolder(CreateFolderRequest request) {
        Folder folder = new Folder();
        folder.setParentId(parseUuid(request.getParentId()));
        folder.setName(required(request.getName(), "資料夾名稱不可為空 / Folder name is required"));
        folder.setOwnerId(request.getOwnerId());
        folder.setPath(buildFolderPath(folder.getParentId(), folder.getName()));
        return toDTO(folderRepository.save(folder));
    }

    @Override
    public List<FolderDTO> getFolders(String parentId) {
        UUID parsedParentId = parseUuid(parentId);
        if (parsedParentId != null) {
            return folderRepository.findByParentIdAndDeletedAtIsNullOrderByNameAsc(parsedParentId).stream().map(this::toDTO).toList();
        }
        return folderRepository.findByDeletedAtIsNullOrderByPathAsc().stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional
    public FolderDTO updateFolder(String folderId, UpdateFolderRequest request) {
        Folder folder = findFolder(folderId);
        folder.setName(required(request.getName(), "資料夾名稱不可為空 / Folder name is required"));
        folder.setPath(buildFolderPath(folder.getParentId(), folder.getName()));
        return toDTO(folderRepository.save(folder));
    }

    @Override
    @Transactional
    public void deleteFolder(String folderId) {
        Folder folder = findFolder(folderId);
        folder.setDeletedAt(LocalDateTime.now());
        folderRepository.save(folder);
    }

    @Override
    @Transactional
    @Auditable(module = "document", action = "REGISTER_DOCUMENT")
    public DocumentDTO registerDocument(RegisterDocumentRequest request) {
        Document document = new Document();
        document.setFolderId(parseUuid(request.getFolderId()));
        document.setFileName(required(request.getFileName(), "文件名稱不可為空 / File name is required"));
        document.setFilePath(safeStorageKey(request.getFilePath()));
        document.setMimeType(defaultIfBlank(request.getMimeType(), "application/octet-stream"));
        document.setSize(request.getSize() == null ? 0L : request.getSize());
        document.setOwnerId(request.getOwnerId());
        Document savedDocument = documentRepository.save(document);
        versionRepository.save(toInitialVersion(savedDocument));
        return toDTO(savedDocument);
    }

    @Override
    public List<DocumentDTO> getDocuments(String folderId) {
        UUID parsedFolderId = parseUuid(folderId);
        List<Document> documents = parsedFolderId == null
                ? documentRepository.findByDeletedAtIsNullOrderByCreatedAtDesc()
                : documentRepository.findByFolderIdAndDeletedAtIsNullOrderByCreatedAtDesc(parsedFolderId);
        return documents.stream().map(this::toDTO).toList();
    }

    @Override
    public DocumentDTO getDocument(String documentId) {
        return toDTO(findDocument(documentId));
    }

    @Override
    @Transactional
    @Auditable(module = "document", action = "REGISTER_DOCUMENT_VERSION")
    public DocumentVersionDTO registerDocumentVersion(String documentId, RegisterDocumentVersionRequest request) {
        Document document = findDocument(documentId);
        int nextVersion = document.getVersion() + 1;
        document.setVersion(nextVersion);
        document.setFilePath(safeStorageKey(request.getFilePath()));
        document.setMimeType(defaultIfBlank(request.getMimeType(), document.getMimeType()));
        document.setSize(request.getSize() == null ? document.getSize() : request.getSize());
        documentRepository.save(document);

        DocumentVersion version = new DocumentVersion();
        version.setDocumentId(document.getId());
        version.setVersion(nextVersion);
        version.setFilePath(document.getFilePath());
        version.setMimeType(document.getMimeType());
        version.setSize(document.getSize());
        version.setUploadedBy(request.getUploadedBy());
        return toDTO(versionRepository.save(version));
    }

    @Override
    public List<DocumentVersionDTO> getVersions(String documentId) {
        UUID id = findDocument(documentId).getId();
        return versionRepository.findByDocumentIdAndDeletedAtIsNullOrderByVersionDesc(id).stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional
    @Auditable(module = "document", action = "SHARE_DOCUMENT")
    public DocumentShareDTO shareDocument(String documentId, ShareDocumentRequest request) {
        Document document = findDocument(documentId);
        DocumentShare share = shareRepository
                .findFirstByDocumentIdAndSharedWithAndDeletedAtIsNull(document.getId(), required(request.getSharedWith(), "分享對象不可為空 / Shared user is required"))
                .orElseGet(DocumentShare::new);
        share.setDocumentId(document.getId());
        share.setSharedWith(request.getSharedWith());
        share.setPermission(request.getPermission() == null ? Permission.READ : request.getPermission());
        share.setSharedBy(request.getSharedBy());
        share.setExpiresAt(request.getExpiresAt());
        return toDTO(shareRepository.save(share));
    }

    @Override
    public List<DocumentShareDTO> getShares(String documentId) {
        UUID id = findDocument(documentId).getId();
        return shareRepository.findByDocumentIdAndDeletedAtIsNullOrderByCreatedAtDesc(id).stream().map(this::toDTO).toList();
    }

    @Override
    public DocumentAccessDTO checkAccess(String documentId, String userId, Permission requiredPermission) {
        Document document = findDocument(documentId);
        Permission permission = requiredPermission == null ? Permission.READ : requiredPermission;
        boolean allowed = document.getOwnerId() != null && document.getOwnerId().equals(userId);
        if (!allowed) {
            allowed = shareRepository.findFirstByDocumentIdAndSharedWithAndDeletedAtIsNull(document.getId(), userId)
                    .filter(share -> share.getExpiresAt() == null || share.getExpiresAt().isAfter(LocalDateTime.now()))
                    .filter(share -> hasPermission(share.getPermission(), permission))
                    .isPresent();
        }
        return DocumentAccessDTO.builder()
                .documentId(document.getId().toString())
                .userId(userId)
                .requiredPermission(permission)
                .allowed(allowed)
                .build();
    }

    @Override
    @Transactional
    public DocumentTagDTO assignTag(String documentId, AssignTagRequest request) {
        Document document = findDocument(documentId);
        String tagName = required(request.getName(), "標籤名稱不可為空 / Tag name is required");
        DocumentTag tag = tagRepository.findFirstByDocumentIdAndNameAndDeletedAtIsNull(document.getId(), tagName)
                .orElseGet(DocumentTag::new);
        tag.setDocumentId(document.getId());
        tag.setName(tagName);
        tag.setColor(defaultIfBlank(request.getColor(), "#64748b"));
        return toDTO(tagRepository.save(tag));
    }

    @Override
    public List<DocumentTagDTO> getTags(String documentId) {
        UUID id = findDocument(documentId).getId();
        return tagRepository.findByDocumentIdAndDeletedAtIsNullOrderByNameAsc(id).stream().map(this::toDTO).toList();
    }

    private Folder findFolder(String folderId) {
        return folderRepository.findById(UUID.fromString(folderId))
                .filter(folder -> folder.getDeletedAt() == null)
                .orElseThrow(() -> new BusinessException(404, "資料夾不存在 / Folder not found"));
    }

    private Document findDocument(String documentId) {
        return documentRepository.findById(UUID.fromString(documentId))
                .filter(document -> document.getDeletedAt() == null)
                .orElseThrow(() -> new BusinessException(404, "文件不存在 / Document not found"));
    }

    private DocumentVersion toInitialVersion(Document document) {
        DocumentVersion version = new DocumentVersion();
        version.setDocumentId(document.getId());
        version.setVersion(document.getVersion());
        version.setFilePath(document.getFilePath());
        version.setMimeType(document.getMimeType());
        version.setSize(document.getSize());
        version.setUploadedBy(document.getOwnerId());
        return version;
    }

    private String buildFolderPath(UUID parentId, String name) {
        if (parentId == null) {
            return "/" + name;
        }
        Folder parent = folderRepository.findById(parentId)
                .filter(folder -> folder.getDeletedAt() == null)
                .orElseThrow(() -> new BusinessException(404, "上層資料夾不存在 / Parent folder not found"));
        return parent.getPath() + "/" + name;
    }

    private boolean hasPermission(Permission actual, Permission required) {
        return permissionLevel(actual) >= permissionLevel(required);
    }

    private int permissionLevel(Permission permission) {
        if (permission == Permission.SHARE) {
            return 3;
        }
        if (permission == Permission.EDIT) {
            return 2;
        }
        return 1;
    }

    private UUID parseUuid(String value) {
        return value == null || value.isBlank() ? null : UUID.fromString(value);
    }

    private String required(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new BusinessException(400, message);
        }
        return value.trim();
    }

    private String defaultIfBlank(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }

    private String safeStorageKey(String value) {
        String storageKey = required(value, "文件儲存鍵不可為空 / File storage key is required");
        if (storageKey.startsWith("/") || storageKey.contains("..")) {
            throw new BusinessException(400, "文件儲存鍵不可使用絕對路徑或上層路徑 / Storage key cannot be absolute or contain parent traversal");
        }
        return storageKey;
    }

    private FolderDTO toDTO(Folder folder) {
        return FolderDTO.builder()
                .id(folder.getId() != null ? folder.getId().toString() : null)
                .parentId(folder.getParentId() != null ? folder.getParentId().toString() : null)
                .name(folder.getName())
                .path(folder.getPath())
                .ownerId(folder.getOwnerId())
                .build();
    }

    private DocumentDTO toDTO(Document document) {
        return DocumentDTO.builder()
                .id(document.getId() != null ? document.getId().toString() : null)
                .folderId(document.getFolderId() != null ? document.getFolderId().toString() : null)
                .fileName(document.getFileName())
                .filePath(document.getFilePath())
                .mimeType(document.getMimeType())
                .size(document.getSize())
                .version(document.getVersion())
                .ownerId(document.getOwnerId())
                .tags(document.getId() == null ? List.of() : tagRepository.findByDocumentIdAndDeletedAtIsNullOrderByNameAsc(document.getId()).stream().map(this::toDTO).toList())
                .build();
    }

    private DocumentVersionDTO toDTO(DocumentVersion version) {
        return DocumentVersionDTO.builder()
                .id(version.getId() != null ? version.getId().toString() : null)
                .documentId(version.getDocumentId().toString())
                .version(version.getVersion())
                .filePath(version.getFilePath())
                .mimeType(version.getMimeType())
                .size(version.getSize())
                .uploadedBy(version.getUploadedBy())
                .build();
    }

    private DocumentShareDTO toDTO(DocumentShare share) {
        return DocumentShareDTO.builder()
                .id(share.getId() != null ? share.getId().toString() : null)
                .documentId(share.getDocumentId().toString())
                .sharedWith(share.getSharedWith())
                .permission(share.getPermission())
                .sharedBy(share.getSharedBy())
                .expiresAt(share.getExpiresAt())
                .build();
    }

    private DocumentTagDTO toDTO(DocumentTag tag) {
        return DocumentTagDTO.builder()
                .id(tag.getId() != null ? tag.getId().toString() : null)
                .documentId(tag.getDocumentId().toString())
                .name(tag.getName())
                .color(tag.getColor())
                .build();
    }
}
