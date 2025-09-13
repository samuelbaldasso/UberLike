package com.sbaldasso.combobackend.modules.tracking.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LocationUpdateDTO {
    private Long driverId;
    private Long deliveryId;
    private Double latitude;
    private Double longitude;
    private Double speed;
    private Double heading;
    private LocalDateTime timestamp;
}
