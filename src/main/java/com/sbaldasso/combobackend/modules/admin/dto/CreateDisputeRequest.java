package com.sbaldasso.combobackend.modules.admin.dto;

import com.sbaldasso.combobackend.modules.admin.domain.Dispute.DisputeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateDisputeRequest {
    @NotNull(message = "ID da entrega é obrigatório")
    private UUID deliveryId;

    @NotNull(message = "Tipo da disputa é obrigatório")
    private DisputeType type;

    @NotBlank(message = "Descrição é obrigatória")
    @Size(min = 10, max = 1000, message = "Descrição deve ter entre 10 e 1000 caracteres")
    private String description;
}
