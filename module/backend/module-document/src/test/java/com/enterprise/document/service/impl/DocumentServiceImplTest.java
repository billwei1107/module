package com.enterprise.document.service.impl;

import com.enterprise.common.exception.BusinessException;
import com.enterprise.document.dto.RegisterDocumentRequest;
import com.enterprise.document.dto.RegisterDocumentVersionRequest;
import com.enterprise.document.dto.ShareDocumentRequest;
import com.enterprise.document.entity.Document;
import com.enterprise.document.entity.DocumentShare;
import com.enterprise.document.entity.DocumentShare.Permission;
import com.enterprise.document.entity.DocumentVersion;
import com.enterprise.document.repository.*;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @file DocumentServiceImplTest.java
 * @description 文件管理服務測試 / Document service tests
 */
class DocumentServiceImplTest {

    @Test
    void registerDocumentShouldCreateInitialVersion() {
        DocumentRepository documentRepository = mock(DocumentRepository.class);
        DocumentVersionRepository versionRepository = mock(DocumentVersionRepository.class);
        UUID documentId = UUID.randomUUID();
        when(documentRepository.save(any(Document.class))).thenAnswer(invocation -> {
            Document document = invocation.getArgument(0);
            document.setId(documentId);
            return document;
        });
        when(versionRepository.save(any(DocumentVersion.class))).thenAnswer(invocation -> invocation.getArgument(0));
        DocumentServiceImpl service = service(documentRepository, versionRepository, mock(DocumentShareRepository.class));

        RegisterDocumentRequest request = new RegisterDocumentRequest();
        request.setFileName("contract.pdf");
        request.setFilePath("documents/contract-v1.pdf");
        request.setMimeType("application/pdf");
        request.setSize(100L);
        request.setOwnerId("emp-001");

        assertThat(service.registerDocument(request).getVersion()).isEqualTo(1);
        verify(versionRepository).save(argThat(version -> version.getDocumentId().equals(documentId) && version.getVersion() == 1));
    }

    @Test
    void registerDocumentShouldRejectAbsoluteStorageKey() {
        DocumentServiceImpl service = service(mock(DocumentRepository.class), mock(DocumentVersionRepository.class), mock(DocumentShareRepository.class));
        RegisterDocumentRequest request = new RegisterDocumentRequest();
        request.setFileName("contract.pdf");
        request.setFilePath("/var/data/contract.pdf");

        assertThatThrownBy(() -> service.registerDocument(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("儲存鍵");
    }

    @Test
    void registerVersionShouldIncrementDocumentVersionAndPersistImmutableVersion() {
        DocumentRepository documentRepository = mock(DocumentRepository.class);
        DocumentVersionRepository versionRepository = mock(DocumentVersionRepository.class);
        UUID documentId = UUID.randomUUID();
        Document document = document(documentId, "emp-001");
        when(documentRepository.findById(documentId)).thenReturn(Optional.of(document));
        when(documentRepository.save(any(Document.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(versionRepository.save(any(DocumentVersion.class))).thenAnswer(invocation -> invocation.getArgument(0));
        DocumentServiceImpl service = service(documentRepository, versionRepository, mock(DocumentShareRepository.class));

        RegisterDocumentVersionRequest request = new RegisterDocumentVersionRequest();
        request.setFilePath("documents/contract-v2.pdf");
        request.setMimeType("application/pdf");
        request.setSize(200L);
        request.setUploadedBy("emp-002");

        assertThat(service.registerDocumentVersion(documentId.toString(), request).getVersion()).isEqualTo(2);
        assertThat(document.getVersion()).isEqualTo(2);
        verify(versionRepository).save(argThat(version -> version.getVersion() == 2 && version.getUploadedBy().equals("emp-002")));
    }

    @Test
    void checkAccessShouldAllowSharePermissionHierarchy() {
        DocumentRepository documentRepository = mock(DocumentRepository.class);
        DocumentShareRepository shareRepository = mock(DocumentShareRepository.class);
        UUID documentId = UUID.randomUUID();
        DocumentShare share = new DocumentShare();
        share.setDocumentId(documentId);
        share.setSharedWith("emp-002");
        share.setPermission(Permission.EDIT);
        when(documentRepository.findById(documentId)).thenReturn(Optional.of(document(documentId, "emp-001")));
        when(shareRepository.findFirstByDocumentIdAndSharedWithAndDeletedAtIsNull(documentId, "emp-002")).thenReturn(Optional.of(share));
        DocumentServiceImpl service = service(documentRepository, mock(DocumentVersionRepository.class), shareRepository);

        assertThat(service.checkAccess(documentId.toString(), "emp-002", Permission.READ).getAllowed()).isTrue();
        assertThat(service.checkAccess(documentId.toString(), "emp-002", Permission.SHARE).getAllowed()).isFalse();
    }

    @Test
    void shareDocumentShouldUpsertExistingShare() {
        DocumentRepository documentRepository = mock(DocumentRepository.class);
        DocumentShareRepository shareRepository = mock(DocumentShareRepository.class);
        UUID documentId = UUID.randomUUID();
        Document document = document(documentId, "emp-001");
        DocumentShare existingShare = new DocumentShare();
        existingShare.setDocumentId(documentId);
        existingShare.setSharedWith("emp-002");
        existingShare.setPermission(Permission.READ);
        when(documentRepository.findById(documentId)).thenReturn(Optional.of(document));
        when(shareRepository.findFirstByDocumentIdAndSharedWithAndDeletedAtIsNull(documentId, "emp-002")).thenReturn(Optional.of(existingShare));
        when(shareRepository.save(any(DocumentShare.class))).thenAnswer(invocation -> invocation.getArgument(0));
        DocumentServiceImpl service = service(documentRepository, mock(DocumentVersionRepository.class), shareRepository);

        ShareDocumentRequest request = new ShareDocumentRequest();
        request.setSharedWith("emp-002");
        request.setPermission(Permission.SHARE);

        assertThat(service.shareDocument(documentId.toString(), request).getPermission()).isEqualTo(Permission.SHARE);
    }

    private DocumentServiceImpl service(
            DocumentRepository documentRepository,
            DocumentVersionRepository versionRepository,
            DocumentShareRepository shareRepository) {
        return new DocumentServiceImpl(
                mock(FolderRepository.class),
                documentRepository,
                versionRepository,
                shareRepository,
                mock(DocumentTagRepository.class));
    }

    private Document document(UUID documentId, String ownerId) {
        Document document = new Document();
        document.setId(documentId);
        document.setFileName("contract.pdf");
        document.setFilePath("documents/contract-v1.pdf");
        document.setMimeType("application/pdf");
        document.setSize(100L);
        document.setVersion(1);
        document.setOwnerId(ownerId);
        return document;
    }
}
