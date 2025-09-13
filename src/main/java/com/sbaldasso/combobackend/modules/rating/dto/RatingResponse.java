package com.sbaldasso.combobackend.modules.rating.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class RatingResponse {
    private UUID id;
    private UUID deliveryId;
    private UUID reviewerId;
    private UUID ratedUserId;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}
