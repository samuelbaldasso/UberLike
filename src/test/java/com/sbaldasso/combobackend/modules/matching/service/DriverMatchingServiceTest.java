package com.sbaldasso.combobackend.modules.matching.service;

import com.sbaldasso.combobackend.modules.delivery.domain.Delivery;
import com.sbaldasso.combobackend.modules.delivery.domain.DeliveryStatus;
import com.sbaldasso.combobackend.modules.location.domain.Location;
import com.sbaldasso.combobackend.modules.location.repository.LocationRepository;
import com.sbaldasso.combobackend.modules.matching.dto.MatchingRequest;
import com.sbaldasso.combobackend.modules.matching.dto.MatchingResponse;
import com.sbaldasso.combobackend.modules.user.domain.Driver;
import com.sbaldasso.combobackend.modules.user.repository.DriverRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.*;

class DriverMatchingServiceTest {

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @InjectMocks
    private DriverMatchingService matchingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findMatch_shouldReturnClosestAvailableDriver() {
        // Arrange
        MatchingRequest request = createSampleRequest();
        List<Location> nearbyDrivers = createNearbyDriversList();
        Driver selectedDriver = createDriver(1L);

        when(locationRepository.findNearbyDrivers(anyDouble(), anyDouble(), anyDouble()))
            .thenReturn(nearbyDrivers);
        when(driverRepository.findById(1L))
            .thenReturn(Optional.of(selectedDriver));

        // Act
        MatchingResponse response = matchingService.findMatch(request);

        // Assert
        assertNotNull(response);
        assertEquals(selectedDriver.getId(), response.getDriverId());
        assertTrue(response.getEstimatedArrivalTime() > 0);
    }

    @Test
    void findMatch_shouldExcludeUnavailableDrivers() {
        // Arrange
        MatchingRequest request = createSampleRequest();
        List<Location> nearbyDrivers = createMixedDriversList();
        Driver availableDriver = createDriver(2L);

        when(locationRepository.findNearbyDrivers(anyDouble(), anyDouble(), anyDouble()))
            .thenReturn(nearbyDrivers);
        when(driverRepository.findById(2L))
            .thenReturn(Optional.of(availableDriver));

        // Act
        MatchingResponse response = matchingService.findMatch(request);

        // Assert
        assertNotNull(response);
        assertEquals(availableDriver.getId(), response.getDriverId());
    }

    @Test
    void findMatch_shouldHandleNoAvailableDrivers() {
        // Arrange
        MatchingRequest request = createSampleRequest();
        when(locationRepository.findNearbyDrivers(anyDouble(), anyDouble(), anyDouble()))
            .thenReturn(List.of());

        // Act
        MatchingResponse response = matchingService.findMatch(request);

        // Assert
        assertNull(response.getDriverId());
        assertEquals("NO_DRIVERS_AVAILABLE", response.getStatus());
    }

    @Test
    void findMatch_shouldRespectMaxDistance() {
        // Arrange
        MatchingRequest request = createSampleRequest();
        List<Location> farDrivers = createFarDriversList();

        when(locationRepository.findNearbyDrivers(anyDouble(), anyDouble(), anyDouble()))
            .thenReturn(farDrivers);

        // Act
        MatchingResponse response = matchingService.findMatch(request);

        // Assert
        assertNull(response.getDriverId());
        assertEquals("NO_NEARBY_DRIVERS", response.getStatus());
    }

    @Test
    void findMatch_shouldHandleDriverRejection() {
        // Arrange
        MatchingRequest request = createSampleRequest();
        List<Location> nearbyDrivers = createNearbyDriversList();
        Driver rejectedDriver = createDriver(1L);
        rejectedDriver.setStatus("REJECTED");

        when(locationRepository.findNearbyDrivers(anyDouble(), anyDouble(), anyDouble()))
            .thenReturn(nearbyDrivers);
        when(driverRepository.findById(1L))
            .thenReturn(Optional.of(rejectedDriver));

        // Act
        MatchingResponse response = matchingService.findMatch(request);

        // Assert
        assertNotNull(response);
        assertNotEquals(rejectedDriver.getId(), response.getDriverId());
    }

    @Test
    void findMatch_shouldConsiderDriverRating() {
        // Arrange
        MatchingRequest request = createSampleRequest();
        List<Location> nearbyDrivers = createDriversWithDifferentRatings();
        Driver highlyRatedDriver = createDriverWithRating(2L, 4.8);

        when(locationRepository.findNearbyDrivers(anyDouble(), anyDouble(), anyDouble()))
            .thenReturn(nearbyDrivers);
        when(driverRepository.findById(2L))
            .thenReturn(Optional.of(highlyRatedDriver));

        // Act
        MatchingResponse response = matchingService.findMatch(request);

        // Assert
        assertNotNull(response);
        assertEquals(highlyRatedDriver.getId(), response.getDriverId());
    }

    private MatchingRequest createSampleRequest() {
        return new MatchingRequest(
            -23.550520, -46.633308, // Pickup location
            -23.557821, -46.639680, // Delivery location
            LocalDateTime.now()
        );
    }

    private List<Location> createNearbyDriversList() {
        return Arrays.asList(
            new Location(1L, -23.550520, -46.633308, LocalDateTime.now()),
            new Location(2L, -23.551000, -46.634000, LocalDateTime.now())
        );
    }

    private List<Location> createMixedDriversList() {
        return Arrays.asList(
            new Location(1L, -23.550520, -46.633308, LocalDateTime.now()), // Busy
            new Location(2L, -23.551000, -46.634000, LocalDateTime.now()), // Available
            new Location(3L, -23.552000, -46.635000, LocalDateTime.now())  // Offline
        );
    }

    private List<Location> createFarDriversList() {
        return Arrays.asList(
            new Location(1L, -23.650520, -46.733308, LocalDateTime.now()),
            new Location(2L, -23.651000, -46.734000, LocalDateTime.now())
        );
    }

    private List<Location> createDriversWithDifferentRatings() {
        return Arrays.asList(
            new Location(1L, -23.550520, -46.633308, LocalDateTime.now()), // Rating: 3.5
            new Location(2L, -23.551000, -46.634000, LocalDateTime.now()), // Rating: 4.8
            new Location(3L, -23.552000, -46.635000, LocalDateTime.now())  // Rating: 4.0
        );
    }

    private Driver createDriver(Long id) {
        Driver driver = new Driver();
        driver.setId(id);
        driver.setStatus("AVAILABLE");
        driver.setRating(4.0);
        return driver;
    }

    private Driver createDriverWithRating(Long id, double rating) {
        Driver driver = new Driver();
        driver.setId(id);
        driver.setStatus("AVAILABLE");
        driver.setRating(rating);
        return driver;
    }
}
