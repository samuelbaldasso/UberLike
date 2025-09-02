package com.sbaldasso.combobackend.modules.user.controller;

import com.sbaldasso.combobackend.modules.user.dto.CreateUserRequest;
import com.sbaldasso.combobackend.modules.user.dto.UpdateUserRequest;
import com.sbaldasso.combobackend.modules.user.dto.UserResponse;
import com.sbaldasso.combobackend.modules.user.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {
  @Mock
  private UserServiceImpl userService;
  @InjectMocks
  private UserController userController;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void createUser_returnsUserResponse() {
    CreateUserRequest request = new CreateUserRequest();
    UserResponse response = new UserResponse();
    when(userService.createUser(request)).thenReturn(response);
    ResponseEntity<UserResponse> result = userController.createUser(request);
    assertEquals(response, result.getBody());
    verify(userService).createUser(request);
  }

  @Test
  void updateUser_returnsUserResponse() {
    UUID userId = UUID.randomUUID();
    UpdateUserRequest request = new UpdateUserRequest();
    UserResponse response = new UserResponse();
    when(userService.updateUser(userId, request)).thenReturn(response);
    ResponseEntity<UserResponse> result = userController.updateUser(userId, request);
    assertEquals(response, result.getBody());
    verify(userService).updateUser(userId, request);
  }

  @Test
  void toggleUserActive_returnsOk() {
    UUID userId = UUID.randomUUID();
    ResponseEntity<Void> result = userController.toggleUserActive(userId);
    assertEquals(200, result.getStatusCodeValue());
    verify(userService).toggleUserActive(userId);
  }

  @Test
  void getUsers_returnsPage() {
    // Arrange
    Page<UserResponse> page = new PageImpl<>(Collections.emptyList());
    when(userService.getUsers(any(), any())).thenReturn(page);

    // Act
    ResponseEntity<Page<UserResponse>> result = userController.getUsers(null, Pageable.unpaged());

    // Assert
    assertEquals(page, result.getBody(), "O body do ResponseEntity deve ser igual ao mockado");
    verify(userService).getUsers(null, Pageable.unpaged());
  }

  @Test
  void getUser_returnsUserResponse() {
    UUID userId = UUID.randomUUID();
    UserResponse response = new UserResponse();
    when(userService.getUserById(userId)).thenReturn(response);
    ResponseEntity<UserResponse> result = userController.getUser(userId);
    assertEquals(response, result.getBody());
  }
}
