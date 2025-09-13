package com.sbaldasso.combobackend.modules.admin.controller;

import com.sbaldasso.combobackend.modules.admin.dto.DriverDocumentResponse;
import com.sbaldasso.combobackend.modules.admin.dto.DocumentApprovalRequest;
import com.sbaldasso.combobackend.modules.admin.service.DriverDocumentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DriverDocumentControllerTest {

    @Mock
    private DriverDocumentService driverDocumentService;

    @InjectMocks
    private DriverDocumentController driverDocumentController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void uploadDocument_returnsDriverDocumentResponse() {
        // Arrange
        UUID driverId = UUID.randomUUID();
        String documentType = "CNH";
        MultipartFile file = new MockMultipartFile(
            "document",
            "cnh.pdf",
            "application/pdf",
            "test content".getBytes()
        );

        DriverDocumentResponse expectedResponse = DriverDocumentResponse.builder()
                .id(UUID.randomUUID())
                .driverId(driverId)
                .documentType(documentType)
                .status("PENDING")
                .build();

        when(driverDocumentService.uploadDocument(driverId, documentType, file))
                .thenReturn(expectedResponse);

        // Act
        ResponseEntity<DriverDocumentResponse> response = 
                driverDocumentController.uploadDocument(driverId, documentType, file);

        // Assert
        assertNotNull(response.getBody());
        assertEquals(expectedResponse, response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        verify(driverDocumentService).uploadDocument(driverId, documentType, file);
    }

    @Test
    void approveDocument_returnsDriverDocumentResponse() {
        // Arrange
        UUID documentId = UUID.randomUUID();
        DocumentApprovalRequest request = new DocumentApprovalRequest("APPROVED", "Documentação ok");

        DriverDocumentResponse expectedResponse = DriverDocumentResponse.builder()
                .id(documentId)
                .status("APPROVED")
                .adminComment("Documentação ok")
                .build();

        when(driverDocumentService.updateDocumentStatus(documentId, request))
                .thenReturn(expectedResponse);

        // Act
        ResponseEntity<DriverDocumentResponse> response = 
                driverDocumentController.approveDocument(documentId, request);

        // Assert
        assertNotNull(response.getBody());
        assertEquals(expectedResponse, response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        verify(driverDocumentService).updateDocumentStatus(documentId, request);
    }

    @Test
    void getPendingDocuments_returnsPageOfDocuments() {
        // Arrange
        Page<DriverDocumentResponse> expectedPage = 
                new PageImpl<>(Collections.emptyList());
        
        when(driverDocumentService.getPendingDocuments(Pageable.unpaged()))
                .thenReturn(expectedPage);

        // Act
        ResponseEntity<Page<DriverDocumentResponse>> response = 
                driverDocumentController.getPendingDocuments(Pageable.unpaged());

        // Assert
        assertNotNull(response.getBody());
        assertEquals(expectedPage, response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        verify(driverDocumentService).getPendingDocuments(Pageable.unpaged());
    }

    @Test
    void getDriverDocuments_returnsListOfDocuments() {
        // Arrange
        UUID driverId = UUID.randomUUID();
        Page<DriverDocumentResponse> expectedPage = 
                new PageImpl<>(Collections.emptyList());
        
        when(driverDocumentService.getDriverDocuments(driverId, Pageable.unpaged()))
                .thenReturn(expectedPage);

        // Act
        ResponseEntity<Page<DriverDocumentResponse>> response = 
                driverDocumentController.getDriverDocuments(driverId, Pageable.unpaged());

        // Assert
        assertNotNull(response.getBody());
        assertEquals(expectedPage, response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        verify(driverDocumentService).getDriverDocuments(driverId, Pageable.unpaged());
    }

    @Test
    void rejectDocument_returnsDriverDocumentResponse() {
        // Arrange
        UUID documentId = UUID.randomUUID();
        DocumentApprovalRequest request = 
                new DocumentApprovalRequest("REJECTED", "CNH vencida");

        DriverDocumentResponse expectedResponse = DriverDocumentResponse.builder()
                .id(documentId)
                .status("REJECTED")
                .adminComment("CNH vencida")
                .build();

        when(driverDocumentService.updateDocumentStatus(documentId, request))
                .thenReturn(expectedResponse);

        // Act
        ResponseEntity<DriverDocumentResponse> response = 
                driverDocumentController.rejectDocument(documentId, request);

        // Assert
        assertNotNull(response.getBody());
        assertEquals(expectedResponse, response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        verify(driverDocumentService).updateDocumentStatus(documentId, request);
    }

    @Test
    void downloadDocument_returnsDocument() {
        // Arrange
        UUID documentId = UUID.randomUUID();
        byte[] expectedContent = "test content".getBytes();

        when(driverDocumentService.downloadDocument(documentId))
                .thenReturn(expectedContent);

        // Act
        ResponseEntity<byte[]> response = driverDocumentController.downloadDocument(documentId);

        // Assert
        assertNotNull(response.getBody());
        assertArrayEquals(expectedContent, response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        verify(driverDocumentService).downloadDocument(documentId);
    }
}
