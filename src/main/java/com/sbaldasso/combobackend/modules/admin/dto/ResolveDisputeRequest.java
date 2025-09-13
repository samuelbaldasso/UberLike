package com.sbaldasso.combobackend.modules.admin.dto;

import com.sbaldasso.combobackend.modules.admin.domain.Dispute.DisputeStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResolveDisputeRequest {
    @NotBlank(message = "Resolução é obrigatória")
    @Size(min = 10, max = 1000, message = "Resolução deve ter entre 10 e 1000 caracteres")
    private String resolution;

    private DisputeStatus status;
}
