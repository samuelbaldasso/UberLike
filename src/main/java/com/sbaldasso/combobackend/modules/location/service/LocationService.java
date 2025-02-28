package com.sbaldasso.combobackend.modules.location.service;

import com.sbaldasso.combobackend.modules.location.domain.Location;

import java.util.Optional;
import java.util.UUID;

public interface LocationService {
  Optional<Location> getDriverLocation(UUID driverId);

  Location updateDriverLocation(UUID driverId, Double latitude, Double longitude, Double speed, Double heading);

  void setDriverAvailability(UUID driverId, boolean isAvailable);
}