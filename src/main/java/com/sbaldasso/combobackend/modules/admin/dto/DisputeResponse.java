package com.sbaldasso.combobackend.modules.admin.dto;

import com.sbaldasso.combobackend.modules.admin.domain.Dispute;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class DisputeResponse {
    private UUID id;
    private UUID deliveryId;
    private UUID reporterId;
    private String reporterName;
    private Dispute.DisputeType type;
    private Dispute.DisputeStatus status;
    private String description;
    private String resolution;
    private UUID resolverId;
    private String resolverName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
