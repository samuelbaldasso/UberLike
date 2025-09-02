package com.sbaldasso.combobackend.modules.notification.repository;

import com.sbaldasso.combobackend.modules.notification.domain.Notification;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class NotificationRepositoryTest {
  @Autowired
  private NotificationRepository notificationRepository;

  @Test
  void findByUserIdOrderByCreatedAtDesc_returnsEmptyPageIfNotFound() {
    var page = notificationRepository.findByUserIdOrderByCreatedAtDesc(UUID.randomUUID(),
        org.springframework.data.domain.Pageable.unpaged());
    assertTrue(page.isEmpty());
  }
}
