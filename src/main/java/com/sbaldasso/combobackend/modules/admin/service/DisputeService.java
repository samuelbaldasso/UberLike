package com.sbaldasso.combobackend.modules.admin.service;

import com.sbaldasso.combobackend.modules.admin.domain.Dispute;
import com.sbaldasso.combobackend.modules.admin.dto.CreateDisputeRequest;
import com.sbaldasso.combobackend.modules.admin.dto.DisputeResponse;
import com.sbaldasso.combobackend.modules.admin.dto.ResolveDisputeRequest;
import com.sbaldasso.combobackend.modules.admin.repository.DisputeRepository;
import com.sbaldasso.combobackend.modules.delivery.domain.Delivery;
import com.sbaldasso.combobackend.modules.delivery.service.DeliveryService;
import com.sbaldasso.combobackend.modules.notification.service.WebSocketNotificationService;
import com.sbaldasso.combobackend.modules.user.domain.User;
import com.sbaldasso.combobackend.modules.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DisputeService {

    private final DisputeRepository disputeRepository;
    private final DeliveryService deliveryService;
    private final UserService userService;
    private final WebSocketNotificationService notificationService;

    @Transactional
    public DisputeResponse createDispute(UUID reporterId, CreateDisputeRequest request) {
        User reporter = userService.validateAndGetUser(reporterId);
        Delivery delivery = deliveryService.getDeliveryById(request.getDeliveryId());

        Dispute dispute = new Dispute();
        dispute.setDelivery(delivery);
        dispute.setReporter(reporter);
        dispute.setType(request.getType());
        dispute.setDescription(request.getDescription());
        dispute.setStatus(Dispute.DisputeStatus.PENDING);

        dispute = disputeRepository.save(dispute);
        
        // Notificar partes interessadas
        notifyDisputeCreated(dispute);
        
        return toDisputeResponse(dispute);
    }

    @Transactional
    public DisputeResponse resolveDispute(UUID disputeId, UUID resolverId, ResolveDisputeRequest request) {
        Dispute dispute = getDisputeById(disputeId);
        User resolver = userService.validateAndGetUser(resolverId);

        if (dispute.getStatus() == Dispute.DisputeStatus.RESOLVED || 
            dispute.getStatus() == Dispute.DisputeStatus.CLOSED) {
            throw new IllegalStateException("Disputa já foi resolvida ou fechada");
        }

        dispute.setResolver(resolver);
        dispute.setResolution(request.getResolution());
        dispute.setStatus(request.getStatus());

        dispute = disputeRepository.save(dispute);
        
        // Notificar partes interessadas
        notifyDisputeResolved(dispute);
        
        return toDisputeResponse(dispute);
    }

    @Transactional(readOnly = true)
    public DisputeResponse getDispute(UUID disputeId) {
        return toDisputeResponse(getDisputeById(disputeId));
    }

    @Transactional(readOnly = true)
    public Page<DisputeResponse> getDisputes(Dispute.DisputeStatus status, Pageable pageable) {
        Page<Dispute> disputes = status != null ?
                disputeRepository.findByStatus(status, pageable) :
                disputeRepository.findAll(pageable);
        return disputes.map(this::toDisputeResponse);
    }

    @Transactional(readOnly = true)
    public Page<DisputeResponse> getUserDisputes(UUID userId, Pageable pageable) {
        return disputeRepository.findByReporterId(userId, pageable)
                .map(this::toDisputeResponse);
    }

    private Dispute getDisputeById(UUID disputeId) {
        return disputeRepository.findById(disputeId)
                .orElseThrow(() -> new EntityNotFoundException("Disputa não encontrada"));
    }

    private DisputeResponse toDisputeResponse(Dispute dispute) {
        return DisputeResponse.builder()
                .id(dispute.getId())
                .deliveryId(dispute.getDelivery().getId())
                .reporterId(dispute.getReporter().getId())
                .reporterName(dispute.getReporter().getName())
                .type(dispute.getType())
                .status(dispute.getStatus())
                .description(dispute.getDescription())
                .resolution(dispute.getResolution())
                .resolverId(dispute.getResolver() != null ? dispute.getResolver().getId() : null)
                .resolverName(dispute.getResolver() != null ? dispute.getResolver().getName() : null)
                .createdAt(dispute.getCreatedAt())
                .updatedAt(dispute.getUpdatedAt())
                .build();
    }

    private void notifyDisputeCreated(Dispute dispute) {
        // TODO: Implementar notificação via WebSocket para administradores
        // notificationService.notifyAdmins("Nova disputa criada: " + dispute.getId());
    }

    private void notifyDisputeResolved(Dispute dispute) {
        // TODO: Implementar notificação via WebSocket para as partes envolvidas
        // notificationService.notifyUser(dispute.getReporter().getId(), "Sua disputa foi resolvida");
    }
}
