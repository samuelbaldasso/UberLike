package com.sbaldasso.combobackend.modules.delivery.repository;

import com.sbaldasso.combobackend.modules.delivery.domain.Delivery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, UUID> {
  Page<Delivery> findByDriverId(UUID driverId, Pageable pageable);

  Page<Delivery> findByCustomerId(UUID customerId, Pageable pageable);
}