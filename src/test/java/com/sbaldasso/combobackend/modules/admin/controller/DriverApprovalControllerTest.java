package com.sbaldasso.combobackend.modules.admin.controller;

import com.sbaldasso.combobackend.modules.admin.dto.DriverApprovalResponse;
import com.sbaldasso.combobackend.modules.admin.dto.UpdateDriverApprovalRequest;
import com.sbaldasso.combobackend.modules.admin.service.DriverApprovalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DriverApprovalControllerTest {

    @Mock
    private DriverApprovalService driverApprovalService;

    @InjectMocks
    private DriverApprovalController driverApprovalController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getPendingApprovals_returnsPageOfApprovals() {
        // Arrange
        Page<DriverApprovalResponse> expectedPage = new PageImpl<>(Collections.emptyList());
        when(driverApprovalService.getPendingApprovals(Pageable.unpaged())).thenReturn(expectedPage);

        // Act
        ResponseEntity<Page<DriverApprovalResponse>> response = 
            driverApprovalController.getPendingApprovals(Pageable.unpaged());

        // Assert
        assertNotNull(response.getBody());
        assertEquals(expectedPage, response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        verify(driverApprovalService).getPendingApprovals(Pageable.unpaged());
    }

    @Test
    void approveDriver_returnsApprovalResponse() {
        // Arrange
        UUID driverId = UUID.randomUUID();
        UpdateDriverApprovalRequest request = UpdateDriverApprovalRequest.builder()
                .status("APPROVED")
                .adminComment("Todos os documentos verificados e aprovados")
                .build();

        DriverApprovalResponse expectedResponse = DriverApprovalResponse.builder()
                .id(UUID.randomUUID())
                .driverId(driverId)
                .status("APPROVED")
                .adminComment(request.getAdminComment())
                .approvedAt(LocalDateTime.now())
                .build();

        when(driverApprovalService.updateDriverApproval(driverId, request))
                .thenReturn(expectedResponse);

        // Act
        ResponseEntity<DriverApprovalResponse> response = 
            driverApprovalController.approveDriver(driverId, request);

        // Assert
        assertNotNull(response.getBody());
        assertEquals(expectedResponse, response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        verify(driverApprovalService).updateDriverApproval(driverId, request);
    }

    @Test
    void rejectDriver_returnsApprovalResponse() {
        // Arrange
        UUID driverId = UUID.randomUUID();
        UpdateDriverApprovalRequest request = UpdateDriverApprovalRequest.builder()
                .status("REJECTED")
                .adminComment("CNH vencida")
                .build();

        DriverApprovalResponse expectedResponse = DriverApprovalResponse.builder()
                .id(UUID.randomUUID())
                .driverId(driverId)
                .status("REJECTED")
                .adminComment(request.getAdminComment())
                .rejectedAt(LocalDateTime.now())
                .build();

        when(driverApprovalService.updateDriverApproval(driverId, request))
                .thenReturn(expectedResponse);

        // Act
        ResponseEntity<DriverApprovalResponse> response = 
            driverApprovalController.rejectDriver(driverId, request);

        // Assert
        assertNotNull(response.getBody());
        assertEquals(expectedResponse, response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        verify(driverApprovalService).updateDriverApproval(driverId, request);
    }

    @Test
    void requestAdditionalDocuments_returnsApprovalResponse() {
        // Arrange
        UUID driverId = UUID.randomUUID();
        UpdateDriverApprovalRequest request = UpdateDriverApprovalRequest.builder()
                .status("PENDING_DOCUMENTS")
                .adminComment("Necessário enviar comprovante de residência atualizado")
                .requiredDocuments(Collections.singletonList("PROOF_OF_ADDRESS"))
                .build();

        DriverApprovalResponse expectedResponse = DriverApprovalResponse.builder()
                .id(UUID.randomUUID())
                .driverId(driverId)
                .status("PENDING_DOCUMENTS")
                .adminComment(request.getAdminComment())
                .requiredDocuments(request.getRequiredDocuments())
                .build();

        when(driverApprovalService.updateDriverApproval(driverId, request))
                .thenReturn(expectedResponse);

        // Act
        ResponseEntity<DriverApprovalResponse> response = 
            driverApprovalController.requestAdditionalDocuments(driverId, request);

        // Assert
        assertNotNull(response.getBody());
        assertEquals(expectedResponse, response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        verify(driverApprovalService).updateDriverApproval(driverId, request);
    }

    @Test
    void getDriverApprovalStatus_returnsApprovalResponse() {
        // Arrange
        UUID driverId = UUID.randomUUID();
        DriverApprovalResponse expectedResponse = DriverApprovalResponse.builder()
                .id(UUID.randomUUID())
                .driverId(driverId)
                .status("PENDING")
                .build();

        when(driverApprovalService.getDriverApprovalStatus(driverId))
                .thenReturn(expectedResponse);

        // Act
        ResponseEntity<DriverApprovalResponse> response = 
            driverApprovalController.getDriverApprovalStatus(driverId);

        // Assert
        assertNotNull(response.getBody());
        assertEquals(expectedResponse, response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        verify(driverApprovalService).getDriverApprovalStatus(driverId);
    }

    @Test
    void getApprovalHistory_returnsPageOfApprovals() {
        // Arrange
        UUID driverId = UUID.randomUUID();
        Page<DriverApprovalResponse> expectedPage = new PageImpl<>(Collections.emptyList());
        
        when(driverApprovalService.getDriverApprovalHistory(driverId, Pageable.unpaged()))
                .thenReturn(expectedPage);

        // Act
        ResponseEntity<Page<DriverApprovalResponse>> response = 
            driverApprovalController.getApprovalHistory(driverId, Pageable.unpaged());

        // Assert
        assertNotNull(response.getBody());
        assertEquals(expectedPage, response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        verify(driverApprovalService).getDriverApprovalHistory(driverId, Pageable.unpaged());
    }
}
