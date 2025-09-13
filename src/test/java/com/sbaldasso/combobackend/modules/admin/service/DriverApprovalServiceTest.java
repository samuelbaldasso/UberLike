package com.sbaldasso.combobackend.modules.admin.service;

import com.sbaldasso.combobackend.modules.admin.domain.DriverApproval;
import com.sbaldasso.combobackend.modules.admin.dto.DriverApprovalResponse;
import com.sbaldasso.combobackend.modules.admin.dto.UpdateDriverApprovalRequest;
import com.sbaldasso.combobackend.modules.admin.repository.DriverApprovalRepository;
import com.sbaldasso.combobackend.modules.notification.service.NotificationService;
import com.sbaldasso.combobackend.modules.user.domain.User;
import com.sbaldasso.combobackend.modules.user.domain.UserStatus;
import com.sbaldasso.combobackend.modules.user.domain.UserType;
import com.sbaldasso.combobackend.modules.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DriverApprovalServiceTest {

    @Mock
    private DriverApprovalRepository approvalRepository;

    @Mock
    private UserService userService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private DriverApprovalService driverApprovalService;

    private User driver;
    private DriverApproval approval;
    private UpdateDriverApprovalRequest updateRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        driver = new User();
        driver.setId(UUID.randomUUID());
        driver.setUserType(UserType.DRIVER);
        driver.setStatus(UserStatus.PENDING_APPROVAL);

        approval = DriverApproval.builder()
                .id(UUID.randomUUID())
                .driver(driver)
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build();

        updateRequest = UpdateDriverApprovalRequest.builder()
                .status("APPROVED")
                .adminComment("Todos os documentos verificados e aprovados")
                .build();
    }

    @Test
    void getPendingApprovals_returnsPageOfApprovals() {
        // Arrange
        Page<DriverApproval> approvalsPage = new PageImpl<>(
            Collections.singletonList(approval));
        
        when(approvalRepository.findByStatus("PENDING", Pageable.unpaged()))
                .thenReturn(approvalsPage);

        // Act
        Page<DriverApprovalResponse> response = driverApprovalService.getPendingApprovals(
            Pageable.unpaged());

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals(approval.getId(), response.getContent().get(0).getId());
    }

    @Test
    void updateDriverApproval_approvesDriver() {
        // Arrange
        when(userService.validateAndGetUser(driver.getId())).thenReturn(driver);
        when(approvalRepository.findLatestByDriverId(driver.getId()))
                .thenReturn(Optional.of(approval));
        when(approvalRepository.save(any(DriverApproval.class)))
                .thenReturn(approval);

        // Act
        DriverApprovalResponse response = driverApprovalService.updateDriverApproval(
            driver.getId(), updateRequest);

        // Assert
        assertNotNull(response);
        assertEquals("APPROVED", response.getStatus());
        assertEquals(updateRequest.getAdminComment(), response.getAdminComment());
        verify(userService).updateUserStatus(driver.getId(), UserStatus.ACTIVE);
        verify(notificationService).sendNotification(
            eq(driver.getId()), 
            contains("aprovado"), 
            any());
    }

    @Test
    void updateDriverApproval_rejectsDriver() {
        // Arrange
        updateRequest.setStatus("REJECTED");
        updateRequest.setAdminComment("CNH vencida");

        when(userService.validateAndGetUser(driver.getId())).thenReturn(driver);
        when(approvalRepository.findLatestByDriverId(driver.getId()))
                .thenReturn(Optional.of(approval));
        when(approvalRepository.save(any(DriverApproval.class)))
                .thenReturn(approval);

        // Act
        DriverApprovalResponse response = driverApprovalService.updateDriverApproval(
            driver.getId(), updateRequest);

        // Assert
        assertNotNull(response);
        assertEquals("REJECTED", response.getStatus());
        assertEquals(updateRequest.getAdminComment(), response.getAdminComment());
        verify(userService).updateUserStatus(driver.getId(), UserStatus.REJECTED);
        verify(notificationService).sendNotification(
            eq(driver.getId()), 
            contains("rejeitado"), 
            any());
    }

    @Test
    void updateDriverApproval_requestsAdditionalDocuments() {
        // Arrange
        updateRequest.setStatus("PENDING_DOCUMENTS");
        updateRequest.setAdminComment("Necessário enviar comprovante de residência atualizado");
        updateRequest.setRequiredDocuments(Collections.singletonList("PROOF_OF_ADDRESS"));

        when(userService.validateAndGetUser(driver.getId())).thenReturn(driver);
        when(approvalRepository.findLatestByDriverId(driver.getId()))
                .thenReturn(Optional.of(approval));
        when(approvalRepository.save(any(DriverApproval.class)))
                .thenReturn(approval);

        // Act
        DriverApprovalResponse response = driverApprovalService.updateDriverApproval(
            driver.getId(), updateRequest);

        // Assert
        assertNotNull(response);
        assertEquals("PENDING_DOCUMENTS", response.getStatus());
        assertEquals(updateRequest.getAdminComment(), response.getAdminComment());
        assertEquals(updateRequest.getRequiredDocuments(), response.getRequiredDocuments());
        verify(userService).updateUserStatus(driver.getId(), UserStatus.PENDING_DOCUMENTS);
        verify(notificationService).sendNotification(
            eq(driver.getId()), 
            contains("documentos adicionais"), 
            any());
    }

    @Test
    void getDriverApprovalStatus_returnsLatestStatus() {
        // Arrange
        when(userService.validateAndGetUser(driver.getId())).thenReturn(driver);
        when(approvalRepository.findLatestByDriverId(driver.getId()))
                .thenReturn(Optional.of(approval));

        // Act
        DriverApprovalResponse response = driverApprovalService.getDriverApprovalStatus(
            driver.getId());

        // Assert
        assertNotNull(response);
        assertEquals(approval.getId(), response.getId());
        assertEquals(approval.getStatus(), response.getStatus());
    }

    @Test
    void getDriverApprovalHistory_returnsPageOfApprovals() {
        // Arrange
        Page<DriverApproval> approvalsPage = new PageImpl<>(
            Collections.singletonList(approval));
        
        when(userService.validateAndGetUser(driver.getId())).thenReturn(driver);
        when(approvalRepository.findByDriverId(driver.getId(), Pageable.unpaged()))
                .thenReturn(approvalsPage);

        // Act
        Page<DriverApprovalResponse> response = driverApprovalService.getDriverApprovalHistory(
            driver.getId(), Pageable.unpaged());

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals(approval.getId(), response.getContent().get(0).getId());
    }

    @Test
    void updateDriverApproval_throwsException_whenUserNotFound() {
        // Arrange
        when(userService.validateAndGetUser(driver.getId()))
                .thenThrow(new IllegalArgumentException());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
            driverApprovalService.updateDriverApproval(driver.getId(), updateRequest));
    }

    @Test
    void updateDriverApproval_throwsException_whenInvalidStatus() {
        // Arrange
        updateRequest.setStatus("INVALID_STATUS");
        
        when(userService.validateAndGetUser(driver.getId())).thenReturn(driver);
        when(approvalRepository.findLatestByDriverId(driver.getId()))
                .thenReturn(Optional.of(approval));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
            driverApprovalService.updateDriverApproval(driver.getId(), updateRequest));
    }

    @Test
    void updateDriverApproval_throwsException_whenUserNotDriver() {
        // Arrange
        driver.setUserType(UserType.CUSTOMER);
        
        when(userService.validateAndGetUser(driver.getId())).thenReturn(driver);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
            driverApprovalService.updateDriverApproval(driver.getId(), updateRequest));
    }
}
