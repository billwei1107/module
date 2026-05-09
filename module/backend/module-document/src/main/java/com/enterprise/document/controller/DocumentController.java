package com.enterprise.document.controller;

import com.enterprise.common.dto.ApiResponse;
import com.enterprise.document.dto.*;
import com.enterprise.document.entity.DocumentShare.Permission;
import com.enterprise.document.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @file DocumentController.java
 * @description 文件管理控制器 / Document management controller
 */
@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @GetMapping("/folders")
    public ApiResponse<List<FolderDTO>> getFolders(@RequestParam(required = false) String parentId) {
        return ApiResponse.success(documentService.getFolders(parentId));
    }

    @PostMapping("/folders")
    public ApiResponse<FolderDTO> createFolder(@RequestBody CreateFolderRequest request) {
        return ApiResponse.success(documentService.createFolder(request));
    }

    @PutMapping("/folders/{id}")
    public ApiResponse<FolderDTO> updateFolder(@PathVariable String id, @RequestBody UpdateFolderRequest request) {
        return ApiResponse.success(documentService.updateFolder(id, request));
    }

    @DeleteMapping("/folders/{id}")
    public ApiResponse<Void> deleteFolder(@PathVariable String id) {
        documentService.deleteFolder(id);
        return ApiResponse.success();
    }

    @GetMapping
    public ApiResponse<List<DocumentDTO>> getDocuments(@RequestParam(required = false) String folderId) {
        return ApiResponse.success(documentService.getDocuments(folderId));
    }

    @PostMapping("/metadata")
    public ApiResponse<DocumentDTO> registerDocument(@RequestBody RegisterDocumentRequest request) {
        return ApiResponse.success(documentService.registerDocument(request));
    }

    @GetMapping("/{id}")
    public ApiResponse<DocumentDTO> getDocument(@PathVariable String id) {
        return ApiResponse.success(documentService.getDocument(id));
    }

    @GetMapping("/{id}/download-info")
    public ApiResponse<DocumentDTO> getDownloadInfo(@PathVariable String id) {
        return ApiResponse.success(documentService.getDocument(id));
    }

    @PostMapping("/{id}/versions/metadata")
    public ApiResponse<DocumentVersionDTO> registerDocumentVersion(@PathVariable String id, @RequestBody RegisterDocumentVersionRequest request) {
        return ApiResponse.success(documentService.registerDocumentVersion(id, request));
    }

    @GetMapping("/{id}/versions")
    public ApiResponse<List<DocumentVersionDTO>> getVersions(@PathVariable String id) {
        return ApiResponse.success(documentService.getVersions(id));
    }

    @PostMapping("/{id}/shares")
    public ApiResponse<DocumentShareDTO> shareDocument(@PathVariable String id, @RequestBody ShareDocumentRequest request) {
        return ApiResponse.success(documentService.shareDocument(id, request));
    }

    @GetMapping("/{id}/shares")
    public ApiResponse<List<DocumentShareDTO>> getShares(@PathVariable String id) {
        return ApiResponse.success(documentService.getShares(id));
    }

    @GetMapping("/{id}/access")
    public ApiResponse<DocumentAccessDTO> checkAccess(
            @PathVariable String id,
            @RequestParam String userId,
            @RequestParam(defaultValue = "READ") Permission permission) {
        return ApiResponse.success(documentService.checkAccess(id, userId, permission));
    }

    @PostMapping("/{id}/tags")
    public ApiResponse<DocumentTagDTO> assignTag(@PathVariable String id, @RequestBody AssignTagRequest request) {
        return ApiResponse.success(documentService.assignTag(id, request));
    }

    @GetMapping("/{id}/tags")
    public ApiResponse<List<DocumentTagDTO>> getTags(@PathVariable String id) {
        return ApiResponse.success(documentService.getTags(id));
    }
}
