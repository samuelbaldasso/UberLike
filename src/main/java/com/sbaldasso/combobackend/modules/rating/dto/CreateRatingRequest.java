package com.sbaldasso.combobackend.modules.rating.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateRatingRequest {
    @Min(value = 1, message = "A nota deve ser no mínimo 1")
    @Max(value = 5, message = "A nota deve ser no máximo 5")
    private Integer rating;

    @Size(max = 1000, message = "O comentário deve ter no máximo 1000 caracteres")
    private String comment;
}
