package com.sbaldasso.combobackend.modules.location.service;

import com.sbaldasso.combobackend.modules.location.domain.Location;
import com.sbaldasso.combobackend.modules.location.repository.LocationRepository;
import com.sbaldasso.combobackend.modules.user.domain.User;
import com.sbaldasso.combobackend.modules.user.service.UserService;
import com.sbaldasso.combobackend.modules.notification.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

  private final LocationRepository locationRepository;
  private final UserService userService;
  private final WebSocketService webSocketService;

  @Override
  @Transactional
  public Location updateDriverLocation(UUID driverId, Double latitude, Double longitude, Double speed, Double heading) {
    User driver = userService.validateAndGetUser(driverId);

    Location location = locationRepository.findByDriverId(driverId)
        .orElseGet(() -> {
          Location newLocation = new Location();
          newLocation.setDriver(driver);
          return newLocation;
        });

    location.setLatitude(latitude);
    location.setLongitude(longitude);
    location.setSpeed(speed);
    location.setHeading(heading);

    Location savedLocation = locationRepository.save(location);
    
    // Send real-time location update via WebSocket
    webSocketService.sendLocationUpdate(driverId.toString(), savedLocation);
    
    return savedLocation;
  }

  @Override
  @Transactional
  public void setDriverAvailability(UUID driverId, boolean isAvailable) {
    Location location = locationRepository.findByDriverId(driverId)
        .orElseThrow(() -> new IllegalStateException("Driver location not found"));

    location.setAvailable(isAvailable);
    locationRepository.save(location);
  }

  @Override
  public Optional<Location> getDriverLocation(UUID driverId) {
    return locationRepository.findByDriverId(driverId);
  }
}