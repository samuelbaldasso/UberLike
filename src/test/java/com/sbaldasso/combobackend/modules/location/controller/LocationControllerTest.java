package com.sbaldasso.combobackend.modules.location.controller;

import com.sbaldasso.combobackend.modules.location.domain.Location;
import com.sbaldasso.combobackend.modules.location.dto.UpdateLocationRequest;
import com.sbaldasso.combobackend.modules.location.service.LocationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LocationControllerTest {
  @Mock
  private LocationService locationService;
  @InjectMocks
  private LocationController locationController;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void updateLocation_returnsLocation() {
    UUID userId = UUID.randomUUID();
    UpdateLocationRequest request = new UpdateLocationRequest();
    Location location = new Location();
    when(locationService.updateDriverLocation(userId, null, null, null, null)).thenReturn(location);
    ResponseEntity<Location> result = locationController.updateLocation(userId, request);
    assertEquals(location, result.getBody());
  }

  @Test
  void updateAvailability_returnsOk() {
    UUID userId = UUID.randomUUID();
    ResponseEntity<Void> result = locationController.updateAvailability(userId, true);
    assertEquals(200, result.getStatusCodeValue());
    verify(locationService).setDriverAvailability(userId, true);
  }

  @Test
  void getDriverLocation_returnsLocationOrNotFound() {
    UUID driverId = UUID.randomUUID();
    Location location = new Location();
    when(locationService.getDriverLocation(driverId)).thenReturn(Optional.of(location));
    ResponseEntity<Location> found = locationController.getDriverLocation(driverId);
    assertEquals(location, found.getBody());
    when(locationService.getDriverLocation(driverId)).thenReturn(Optional.empty());
    ResponseEntity<Location> notFound = locationController.getDriverLocation(driverId);
    assertEquals(404, notFound.getStatusCodeValue());
  }
}
