package com.sbaldasso.combobackend.modules.delivery.repository;

import com.sbaldasso.combobackend.modules.delivery.domain.Delivery;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class DeliveryRepositoryTest {
  @Autowired
  private DeliveryRepository deliveryRepository;

  @Test
  void findById_returnsEmptyIfNotFound() {
    Optional<Delivery> delivery = deliveryRepository.findById(UUID.randomUUID());
    assertTrue(delivery.isEmpty());
  }
}
