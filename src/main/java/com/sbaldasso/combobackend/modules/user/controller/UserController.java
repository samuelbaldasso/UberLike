package com.sbaldasso.combobackend.modules.user.controller;

import com.sbaldasso.combobackend.modules.user.domain.UserType;
import com.sbaldasso.combobackend.modules.user.dto.CreateUserRequest;
import com.sbaldasso.combobackend.modules.user.dto.UpdateUserRequest;
import com.sbaldasso.combobackend.modules.user.dto.UserResponse;
import com.sbaldasso.combobackend.modules.user.service.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

  private final UserServiceImpl userService;

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
    return ResponseEntity.ok(userService.createUser(request));
  }

  @PutMapping("/{userId}")
  @PreAuthorize("hasRole('ADMIN') or @securityService.isCurrentUser(#userId)")
  public ResponseEntity<UserResponse> updateUser(
      @PathVariable UUID userId,
      @Valid @RequestBody UpdateUserRequest request) {
    return ResponseEntity.ok(userService.updateUser(userId, request));
  }

  @PutMapping("/{userId}/toggle-active")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> toggleUserActive(@PathVariable UUID userId) {
    userService.toggleUserActive(userId);
    return ResponseEntity.ok().build();
  }

  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Page<UserResponse>> getUsers(
      @RequestParam(required = false) UserType userType,
      Pageable pageable) {
    return ResponseEntity.ok(userService.getUsers(userType, pageable));
  }

  @GetMapping("/{userId}")
  @PreAuthorize("hasRole('ADMIN') or @securityService.isCurrentUser(#userId)")
  public ResponseEntity<UserResponse> getUser(@PathVariable UUID userId) {
    return ResponseEntity.ok(userService.getUserById(userId));
  }
}