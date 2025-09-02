package com.sbaldasso.combobackend.modules.location.service;

import com.sbaldasso.combobackend.modules.location.domain.Location;
import com.sbaldasso.combobackend.modules.location.repository.LocationRepository;
import com.sbaldasso.combobackend.modules.notification.service.WebSocketService;
import com.sbaldasso.combobackend.modules.user.domain.User;
import com.sbaldasso.combobackend.modules.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LocationServiceImplTest {
  @Mock
  private LocationRepository locationRepository;
  @Mock
  private UserService userService;
  @Mock
  private WebSocketService webSocketService;
  @InjectMocks
  private LocationServiceImpl locationService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void updateDriverLocation_savesAndReturnsLocation() {
    UUID driverId = UUID.randomUUID();
    User driver = new User();
    driver.setId(driverId);
    when(userService.validateAndGetUser(driverId)).thenReturn(driver);
    Location location = new Location();
    when(locationRepository.findByDriverId(driverId)).thenReturn(Optional.empty());
    when(locationRepository.save(any(Location.class))).thenReturn(location);
    Location result = locationService.updateDriverLocation(driverId, 1.0, 2.0, 3.0, 4.0);
    assertNotNull(result);
    verify(locationRepository).save(any(Location.class));
    verify(webSocketService).sendLocationUpdate(anyString(), any(Location.class));
  }

  @Test
  void setDriverAvailability_updatesAvailability() {
    UUID driverId = UUID.randomUUID();
    Location location = new Location();
    when(locationRepository.findByDriverId(driverId)).thenReturn(Optional.of(location));
    locationService.setDriverAvailability(driverId, false);
    verify(locationRepository).save(location);
  }

  @Test
  void getDriverLocation_returnsOptional() {
    UUID driverId = UUID.randomUUID();
    Location location = new Location();
    when(locationRepository.findByDriverId(driverId)).thenReturn(Optional.of(location));
    Optional<Location> result = locationService.getDriverLocation(driverId);
    assertTrue(result.isPresent());
  }
}
