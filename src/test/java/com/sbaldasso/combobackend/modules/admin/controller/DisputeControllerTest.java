package com.sbaldasso.combobackend.modules.admin.controller;

import com.sbaldasso.combobackend.modules.admin.dto.CreateDisputeRequest;
import com.sbaldasso.combobackend.modules.admin.dto.DisputeResponse;
import com.sbaldasso.combobackend.modules.admin.dto.UpdateDisputeRequest;
import com.sbaldasso.combobackend.modules.admin.service.DisputeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DisputeControllerTest {

    @Mock
    private DisputeService disputeService;

    @InjectMocks
    private DisputeController disputeController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createDispute_returnsDisputeResponse() {
        // Arrange
        UUID deliveryId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        CreateDisputeRequest request = CreateDisputeRequest.builder()
                .reason("DELIVERY_NOT_RECEIVED")
                .description("A entrega não foi realizada")
                .evidenceUrls(Collections.singletonList("http://evidence.com/photo1.jpg"))
                .build();

        DisputeResponse expectedResponse = DisputeResponse.builder()
                .id(UUID.randomUUID())
                .deliveryId(deliveryId)
                .userId(userId)
                .status("PENDING")
                .reason(request.getReason())
                .description(request.getDescription())
                .evidenceUrls(request.getEvidenceUrls())
                .build();

        when(disputeService.createDispute(deliveryId, userId, request))
                .thenReturn(expectedResponse);

        // Act
        ResponseEntity<DisputeResponse> response = 
                disputeController.createDispute(deliveryId, userId, request);

        // Assert
        assertNotNull(response.getBody());
        assertEquals(expectedResponse, response.getBody());
        assertEquals(201, response.getStatusCodeValue());
        verify(disputeService).createDispute(deliveryId, userId, request);
    }

    @Test
    void updateDisputeStatus_returnsDisputeResponse() {
        // Arrange
        UUID disputeId = UUID.randomUUID();
        UpdateDisputeRequest request = UpdateDisputeRequest.builder()
                .status("RESOLVED")
                .resolution("Cliente será reembolsado")
                .build();

        DisputeResponse expectedResponse = DisputeResponse.builder()
                .id(disputeId)
                .status("RESOLVED")
                .resolution(request.getResolution())
                .build();

        when(disputeService.updateDisputeStatus(disputeId, request))
                .thenReturn(expectedResponse);

        // Act
        ResponseEntity<DisputeResponse> response = 
                disputeController.updateDisputeStatus(disputeId, request);

        // Assert
        assertNotNull(response.getBody());
        assertEquals(expectedResponse, response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        verify(disputeService).updateDisputeStatus(disputeId, request);
    }

    @Test
    void getPendingDisputes_returnsPageOfDisputes() {
        // Arrange
        Page<DisputeResponse> expectedPage = 
                new PageImpl<>(Collections.emptyList());
        
        when(disputeService.getPendingDisputes(Pageable.unpaged()))
                .thenReturn(expectedPage);

        // Act
        ResponseEntity<Page<DisputeResponse>> response = 
                disputeController.getPendingDisputes(Pageable.unpaged());

        // Assert
        assertNotNull(response.getBody());
        assertEquals(expectedPage, response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        verify(disputeService).getPendingDisputes(Pageable.unpaged());
    }

    @Test
    void getUserDisputes_returnsPageOfDisputes() {
        // Arrange
        UUID userId = UUID.randomUUID();
        Page<DisputeResponse> expectedPage = 
                new PageImpl<>(Collections.emptyList());
        
        when(disputeService.getUserDisputes(userId, Pageable.unpaged()))
                .thenReturn(expectedPage);

        // Act
        ResponseEntity<Page<DisputeResponse>> response = 
                disputeController.getUserDisputes(userId, Pageable.unpaged());

        // Assert
        assertNotNull(response.getBody());
        assertEquals(expectedPage, response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        verify(disputeService).getUserDisputes(userId, Pageable.unpaged());
    }

    @Test
    void getDeliveryDisputes_returnsPageOfDisputes() {
        // Arrange
        UUID deliveryId = UUID.randomUUID();
        Page<DisputeResponse> expectedPage = 
                new PageImpl<>(Collections.emptyList());
        
        when(disputeService.getDeliveryDisputes(deliveryId, Pageable.unpaged()))
                .thenReturn(expectedPage);

        // Act
        ResponseEntity<Page<DisputeResponse>> response = 
                disputeController.getDeliveryDisputes(deliveryId, Pageable.unpaged());

        // Assert
        assertNotNull(response.getBody());
        assertEquals(expectedPage, response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        verify(disputeService).getDeliveryDisputes(deliveryId, Pageable.unpaged());
    }

    @Test
    void addComment_returnsDisputeResponse() {
        // Arrange
        UUID disputeId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String comment = "Aguardando evidências adicionais";

        DisputeResponse expectedResponse = DisputeResponse.builder()
                .id(disputeId)
                .status("IN_PROGRESS")
                .latestComment(comment)
                .build();

        when(disputeService.addComment(disputeId, userId, comment))
                .thenReturn(expectedResponse);

        // Act
        ResponseEntity<DisputeResponse> response = 
                disputeController.addComment(disputeId, userId, comment);

        // Assert
        assertNotNull(response.getBody());
        assertEquals(expectedResponse, response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        verify(disputeService).addComment(disputeId, userId, comment);
    }

    @Test
    void getDispute_returnsDisputeResponse() {
        // Arrange
        UUID disputeId = UUID.randomUUID();
        DisputeResponse expectedResponse = DisputeResponse.builder()
                .id(disputeId)
                .status("PENDING")
                .build();

        when(disputeService.getDisputeById(disputeId))
                .thenReturn(expectedResponse);

        // Act
        ResponseEntity<DisputeResponse> response = 
                disputeController.getDispute(disputeId);

        // Assert
        assertNotNull(response.getBody());
        assertEquals(expectedResponse, response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        verify(disputeService).getDisputeById(disputeId);
    }
}
