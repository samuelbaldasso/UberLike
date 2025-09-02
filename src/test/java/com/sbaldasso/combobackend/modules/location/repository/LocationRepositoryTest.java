package com.sbaldasso.combobackend.modules.location.repository;

import com.sbaldasso.combobackend.modules.location.domain.Location;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class LocationRepositoryTest {
  @Autowired
  private LocationRepository locationRepository;

  @Test
  void findByDriverId_returnsEmptyIfNotFound() {
    Optional<Location> location = locationRepository.findByDriverId(UUID.randomUUID());
    assertTrue(location.isEmpty());
  }
}
