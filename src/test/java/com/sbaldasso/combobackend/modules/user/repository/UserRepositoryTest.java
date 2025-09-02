package com.sbaldasso.combobackend.modules.user.repository;

import com.sbaldasso.combobackend.modules.user.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {
  @Autowired
  private UserRepository userRepository;

  @Test
  void findByEmail_returnsEmptyIfNotFound() {
    Optional<User> user = userRepository.findByEmail("notfound@email.com");
    assertTrue(user.isEmpty());
  }
}
