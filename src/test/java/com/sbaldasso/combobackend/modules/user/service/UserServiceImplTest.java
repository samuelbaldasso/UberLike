package com.sbaldasso.combobackend.modules.user.service;

import com.sbaldasso.combobackend.modules.user.domain.User;
import com.sbaldasso.combobackend.modules.user.domain.UserType;
import com.sbaldasso.combobackend.modules.user.dto.CreateUserRequest;
import com.sbaldasso.combobackend.modules.user.dto.UpdateUserRequest;
import com.sbaldasso.combobackend.modules.user.dto.UserResponse;
import com.sbaldasso.combobackend.modules.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {
  @Mock
  private UserRepository userRepository;
  @Mock
  private PasswordEncoder passwordEncoder;
  @InjectMocks
  private UserServiceImpl userService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void createUser_throwsIfEmailExists() {
    CreateUserRequest request = new CreateUserRequest();
    request.setEmail("test@test.com");
    when(userRepository.existsByEmail("test@test.com")).thenReturn(true);
    assertThrows(IllegalStateException.class, () -> userService.createUser(request));
  }

  @Test
  void getUsers_returnsPage() {
    Page<User> users = new PageImpl<>(Collections.emptyList());
    when(userRepository.findAll(any(Pageable.class))).thenReturn(users);
    Page<UserResponse> result = userService.getUsers(null, Pageable.unpaged());
    assertNotNull(result);
  }

  @Test
  void getUserById_returnsUserResponse() {
    UUID userId = UUID.randomUUID();
    User user = new User();
    user.setId(userId);
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    UserResponse response = userService.getUserById(userId);
    assertEquals(userId, response.getId());
  }
}
