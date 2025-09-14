package com.sbaldasso.combobackend.modules.admin.service;

import com.sbaldasso.combobackend.modules.admin.domain.DriverDocument;
import com.sbaldasso.combobackend.modules.admin.dto.DocumentApprovalRequest;
import com.sbaldasso.combobackend.modules.admin.dto.DriverDocumentResponse;
import com.sbaldasso.combobackend.modules.admin.repository.DriverDocumentRepository;
import com.sbaldasso.combobackend.modules.notification.service.NotificationService;
import com.sbaldasso.combobackend.modules.storage.service.StorageService;
import com.sbaldasso.combobackend.modules.user.domain.User;
import com.sbaldasso.combobackend.modules.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DriverDocumentServiceTest {

    @Mock
    private DriverDocumentRepository documentRepository;

    @Mock
    private UserService userService;

    @Mock
    private StorageService storageService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private DriverDocumentService documentService;

    private User driver;
    private DriverDocument document;
    private MultipartFile file;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        driver = new User();
        driver.setId(UUID.randomUUID());

        document = DriverDocument.builder()
                .id(UUID.randomUUID())
                .driver(driver)
                .documentType("CNH")
                .status("PENDING")
                .build();

        file = new MockMultipartFile(
            "document",
            "cnh.pdf",
            "application/pdf",
            "test content".getBytes()
        );
    }

    @Test
    void uploadDocument_savesAndReturnsResponse() {
        // Arrange
        String documentPath = "drivers/" + driver.getId() + "/cnh.pdf";
        when(userService.validateAndGetUser(driver.getId())).thenReturn(driver);
        when(storageService.store(any(), any())).thenReturn(documentPath);
        when(documentRepository.save(any(DriverDocument.class))).thenReturn(document);

        // Act
        DriverDocumentResponse response = documentService.uploadDocument(
            driver.getId(), "CNH", file);

        // Assert
        assertNotNull(response);
        assertEquals(document.getId(), response.getId());
        assertEquals(document.getDocumentType(), response.getDocumentType());
        assertEquals(document.getStatus(), response.getStatus());
        verify(storageService).store(eq(file), anyString());
        verify(documentRepository).save(any(DriverDocument.class));
    }

    @Test
    void updateDocumentStatus_approvesDocument() {
        // Arrange
        DocumentApprovalRequest request = new DocumentApprovalRequest(
            "APPROVED", "Documentação ok");
        
        when(documentRepository.findById(document.getId()))
                .thenReturn(Optional.of(document));
        when(documentRepository.save(any(DriverDocument.class)))
                .thenReturn(document);

        // Act
        DriverDocumentResponse response = documentService.updateDocumentStatus(
            document.getId(), request);

        // Assert
        assertNotNull(response);
        assertEquals("APPROVED", response.getStatus());
        assertEquals(request.getAdminComment(), response.getAdminComment());
        verify(notificationService).sendNotification(
            eq(driver.getId()), 
            contains("aprovado"), 
            any());
    }

    @Test
    void updateDocumentStatus_rejectsDocument() {
        // Arrange
        DocumentApprovalRequest request = new DocumentApprovalRequest(
            "REJECTED", "CNH vencida");
        
        when(documentRepository.findById(document.getId()))
                .thenReturn(Optional.of(document));
        when(documentRepository.save(any(DriverDocument.class)))
                .thenReturn(document);

        // Act
        DriverDocumentResponse response = documentService.updateDocumentStatus(
            document.getId(), request);

        // Assert
        assertNotNull(response);
        assertEquals("REJECTED", response.getStatus());
        assertEquals(request.getAdminComment(), response.getAdminComment());
        verify(notificationService).sendNotification(
            eq(driver.getId()), 
            contains("rejeitado"), 
            any());
    }

    @Test
    void getPendingDocuments_returnsPageOfDocuments() {
        // Arrange
        Page<DriverDocument> documentsPage = new PageImpl<>(
            Collections.singletonList(document));
        
        when(documentRepository.findByStatus("PENDING", Pageable.unpaged()))
                .thenReturn(documentsPage);

        // Act
        Page<DriverDocumentResponse> response = documentService.getPendingDocuments(
            Pageable.unpaged());

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals(document.getId(), response.getContent().get(0).getId());
    }

    @Test
    void getDriverDocuments_returnsPageOfDocuments() {
        // Arrange
        Page<DriverDocument> documentsPage = new PageImpl<>(
            Collections.singletonList(document));
        
        when(documentRepository.findByDriverId(driver.getId(), Pageable.unpaged()))
                .thenReturn(documentsPage);

        // Act
        Page<DriverDocumentResponse> response = documentService.getDriverDocuments(
            driver.getId(), Pageable.unpaged());

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals(document.getId(), response.getContent().get(0).getId());
    }

    @Test
    void downloadDocument_returnsDocumentContent() {
        // Arrange
        byte[] expectedContent = "test content".getBytes();
        
        when(documentRepository.findById(document.getId()))
                .thenReturn(Optional.of(document));
        when(storageService.retrieve(anyString()))
                .thenReturn(expectedContent);

        // Act
        byte[] content = documentService.downloadDocument(document.getId());

        // Assert
        assertNotNull(content);
        assertArrayEquals(expectedContent, content);
    }

    @Test
    void downloadDocument_throwsException_whenDocumentNotFound() {
        // Arrange
        when(documentRepository.findById(document.getId()))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            documentService.downloadDocument(document.getId()));
    }

    @Test
    void uploadDocument_throwsException_whenInvalidDocumentType() {
        // Arrange
        when(userService.validateAndGetUser(driver.getId())).thenReturn(driver);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            documentService.uploadDocument(driver.getId(), "INVALID_TYPE", file));
    }

    @Test
    void updateDocumentStatus_throwsException_whenInvalidStatus() {
        // Arrange
        DocumentApprovalRequest request = new DocumentApprovalRequest(
            "INVALID_STATUS", "Comentário");
        
        when(documentRepository.findById(document.getId()))
                .thenReturn(Optional.of(document));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            documentService.updateDocumentStatus(document.getId(), request));
    }
}
