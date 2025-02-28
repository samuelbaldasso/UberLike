package com.sbaldasso.combobackend.modules.location.repository;

import com.sbaldasso.combobackend.modules.location.domain.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LocationRepository extends JpaRepository<Location, UUID> {
  Optional<Location> findByDriverId(UUID driverId);

  List<Location> findByAvailableTrue();
}