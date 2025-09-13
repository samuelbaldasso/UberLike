package com.sbaldasso.combobackend.modules.admin.service;

import com.sbaldasso.combobackend.modules.admin.domain.Dispute;
import com.sbaldasso.combobackend.modules.admin.domain.DisputeComment;
import com.sbaldasso.combobackend.modules.admin.dto.CreateDisputeRequest;
import com.sbaldasso.combobackend.modules.admin.dto.DisputeResponse;
import com.sbaldasso.combobackend.modules.admin.dto.UpdateDisputeRequest;
import com.sbaldasso.combobackend.modules.admin.repository.DisputeRepository;
import com.sbaldasso.combobackend.modules.delivery.domain.Delivery;
import com.sbaldasso.combobackend.modules.delivery.service.DeliveryService;
import com.sbaldasso.combobackend.modules.notification.service.NotificationService;
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

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DisputeServiceTest {

    @Mock
    private DisputeRepository disputeRepository;

    @Mock
    private UserService userService;

    @Mock
    private DeliveryService deliveryService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private DisputeService disputeService;

    private User user;
    private Delivery delivery;
    private Dispute dispute;
    private CreateDisputeRequest createRequest;
    private UpdateDisputeRequest updateRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(UUID.randomUUID());

        delivery = new Delivery();
        delivery.setId(UUID.randomUUID());
        delivery.setCustomer(user);

        dispute = Dispute.builder()
                .id(UUID.randomUUID())
                .delivery(delivery)
                .user(user)
                .reason("DELIVERY_NOT_RECEIVED")
                .description("A entrega não foi realizada")
                .status("PENDING")
                .build();

        createRequest = CreateDisputeRequest.builder()
                .reason("DELIVERY_NOT_RECEIVED")
                .description("A entrega não foi realizada")
                .evidenceUrls(Collections.singletonList("http://evidence.com/photo1.jpg"))
                .build();

        updateRequest = UpdateDisputeRequest.builder()
                .status("RESOLVED")
                .resolution("Cliente será reembolsado")
                .build();
    }

    @Test
    void createDispute_savesAndReturnsResponse() {
        // Arrange
        when(userService.validateAndGetUser(user.getId())).thenReturn(user);
        when(deliveryService.getDeliveryById(delivery.getId())).thenReturn(delivery);
        when(disputeRepository.save(any(Dispute.class))).thenReturn(dispute);

        // Act
        DisputeResponse response = disputeService.createDispute(
            delivery.getId(), user.getId(), createRequest);

        // Assert
        assertNotNull(response);
        assertEquals(dispute.getId(), response.getId());
        assertEquals(dispute.getReason(), response.getReason());
        assertEquals(dispute.getStatus(), response.getStatus());
        verify(notificationService).sendNotification(any(), any(), any());
        verify(disputeRepository).save(any(Dispute.class));
    }

    @Test
    void updateDisputeStatus_updatesAndReturnsResponse() {
        // Arrange
        when(disputeRepository.findById(dispute.getId()))
                .thenReturn(Optional.of(dispute));
        when(disputeRepository.save(any(Dispute.class)))
                .thenReturn(dispute);

        // Act
        DisputeResponse response = disputeService.updateDisputeStatus(
            dispute.getId(), updateRequest);

        // Assert
        assertNotNull(response);
        assertEquals("RESOLVED", response.getStatus());
        assertEquals(updateRequest.getResolution(), response.getResolution());
        verify(notificationService).sendNotification(
            eq(user.getId()), 
            contains("resolvida"), 
            any());
    }

    @Test
    void getPendingDisputes_returnsPageOfDisputes() {
        // Arrange
        Page<Dispute> disputesPage = new PageImpl<>(
            Collections.singletonList(dispute));
        
        when(disputeRepository.findByStatus("PENDING", Pageable.unpaged()))
                .thenReturn(disputesPage);

        // Act
        Page<DisputeResponse> response = disputeService.getPendingDisputes(
            Pageable.unpaged());

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals(dispute.getId(), response.getContent().get(0).getId());
    }

    @Test
    void getUserDisputes_returnsPageOfDisputes() {
        // Arrange
        Page<Dispute> disputesPage = new PageImpl<>(
            Collections.singletonList(dispute));
        
        when(disputeRepository.findByUserId(user.getId(), Pageable.unpaged()))
                .thenReturn(disputesPage);

        // Act
        Page<DisputeResponse> response = disputeService.getUserDisputes(
            user.getId(), Pageable.unpaged());

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals(dispute.getId(), response.getContent().get(0).getId());
    }

    @Test
    void getDeliveryDisputes_returnsPageOfDisputes() {
        // Arrange
        Page<Dispute> disputesPage = new PageImpl<>(
            Collections.singletonList(dispute));
        
        when(disputeRepository.findByDeliveryId(delivery.getId(), Pageable.unpaged()))
                .thenReturn(disputesPage);

        // Act
        Page<DisputeResponse> response = disputeService.getDeliveryDisputes(
            delivery.getId(), Pageable.unpaged());

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals(dispute.getId(), response.getContent().get(0).getId());
    }

    @Test
    void addComment_addsCommentAndReturnsResponse() {
        // Arrange
        String commentText = "Aguardando evidências adicionais";
        DisputeComment comment = DisputeComment.builder()
                .id(UUID.randomUUID())
                .dispute(dispute)
                .user(user)
                .comment(commentText)
                .build();

        dispute.getComments().add(comment);

        when(disputeRepository.findById(dispute.getId()))
                .thenReturn(Optional.of(dispute));
        when(userService.validateAndGetUser(user.getId()))
                .thenReturn(user);
        when(disputeRepository.save(any(Dispute.class)))
                .thenReturn(dispute);

        // Act
        DisputeResponse response = disputeService.addComment(
            dispute.getId(), user.getId(), commentText);

        // Assert
        assertNotNull(response);
        assertEquals(commentText, response.getLatestComment());
        verify(disputeRepository).save(any(Dispute.class));
        verify(notificationService).sendNotification(any(), any(), any());
    }

    @Test
    void getDisputeById_returnsDispute() {
        // Arrange
        when(disputeRepository.findById(dispute.getId()))
                .thenReturn(Optional.of(dispute));

        // Act
        DisputeResponse response = disputeService.getDisputeById(dispute.getId());

        // Assert
        assertNotNull(response);
        assertEquals(dispute.getId(), response.getId());
        assertEquals(dispute.getStatus(), response.getStatus());
    }

    @Test
    void createDispute_throwsException_whenDeliveryNotFound() {
        // Arrange
        when(userService.validateAndGetUser(user.getId())).thenReturn(user);
        when(deliveryService.getDeliveryById(delivery.getId()))
                .thenThrow(new IllegalArgumentException());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
            disputeService.createDispute(delivery.getId(), user.getId(), createRequest));
    }

    @Test
    void updateDisputeStatus_throwsException_whenDisputeNotFound() {
        // Arrange
        when(disputeRepository.findById(dispute.getId()))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
            disputeService.updateDisputeStatus(dispute.getId(), updateRequest));
    }

    @Test
    void updateDisputeStatus_throwsException_whenInvalidStatus() {
        // Arrange
        UpdateDisputeRequest invalidRequest = UpdateDisputeRequest.builder()
                .status("INVALID_STATUS")
                .resolution("Invalid")
                .build();

        when(disputeRepository.findById(dispute.getId()))
                .thenReturn(Optional.of(dispute));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
            disputeService.updateDisputeStatus(dispute.getId(), invalidRequest));
    }
}
