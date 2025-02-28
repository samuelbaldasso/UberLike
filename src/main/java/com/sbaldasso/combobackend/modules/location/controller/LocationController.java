package com.sbaldasso.combobackend.modules.location.controller;

import com.sbaldasso.combobackend.modules.location.domain.Location;
import com.sbaldasso.combobackend.modules.location.dto.UpdateLocationRequest;
import com.sbaldasso.combobackend.modules.location.service.LocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/locations")
@RequiredArgsConstructor
public class LocationController {

  private final LocationService locationService;

  @PutMapping("/driver")
  @PreAuthorize("hasRole('DRIVER')")
  public ResponseEntity<Location> updateLocation(
      @RequestAttribute UUID userId,
      @Valid @RequestBody UpdateLocationRequest request) {
    return ResponseEntity.ok(locationService.updateDriverLocation(
        userId,
        request.getLatitude(),
        request.getLongitude(),
        request.getSpeed(),
        request.getHeading()));
  }

  @PutMapping("/driver/availability")
  @PreAuthorize("hasRole('DRIVER')")
  public ResponseEntity<Void> updateAvailability(
      @RequestAttribute UUID userId,
      @RequestParam boolean available) {
    locationService.setDriverAvailability(userId, available);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/driver/{driverId}")
  @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
  public ResponseEntity<Location> getDriverLocation(@PathVariable UUID driverId) {
    return locationService.getDriverLocation(driverId)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }
}