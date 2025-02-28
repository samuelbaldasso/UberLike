package com.sbaldasso.combobackend.modules.user.service;

import com.sbaldasso.combobackend.modules.user.domain.User;
import com.sbaldasso.combobackend.modules.user.domain.UserType;
import com.sbaldasso.combobackend.modules.user.dto.CreateUserRequest;
import com.sbaldasso.combobackend.modules.user.dto.UpdateUserRequest;
import com.sbaldasso.combobackend.modules.user.dto.UserResponse;
import com.sbaldasso.combobackend.modules.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public User validateAndGetUser(UUID userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
  }

  @Override
  public User validateAndGetUser(UUID userId, UserType expectedType) {
    User user = validateAndGetUser(userId);
    if (user.getUserType() != expectedType) {
      throw new IllegalStateException("User is not a " + expectedType);
    }
    return user;
  }

  @Override
  public boolean isUserActive(UUID userId) {
    return validateAndGetUser(userId).isActive();
  }

  @Transactional
  public UserResponse createUser(CreateUserRequest request) {
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new IllegalStateException("Email already registered");
    }

    User user = new User();
    user.setName(request.getName());
    user.setEmail(request.getEmail());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setPhone(request.getPhone());
    user.setUserType(request.getUserType());
    user.setActive(true);

    user = userRepository.save(user);
    return toUserResponse(user);
  }

  @Transactional
  public UserResponse updateUser(UUID userId, UpdateUserRequest request) {
    User user = validateAndGetUser(userId);

    if (request.getName() != null) {
      user.setName(request.getName());
    }
    if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
      if (userRepository.existsByEmail(request.getEmail())) {
        throw new IllegalStateException("Email already registered");
      }
      user.setEmail(request.getEmail());
    }
    if (request.getPhone() != null) {
      user.setPhone(request.getPhone());
    }
    if (request.getPassword() != null) {
      user.setPassword(passwordEncoder.encode(request.getPassword()));
    }

    user = userRepository.save(user);
    return toUserResponse(user);
  }

  @Transactional
  public void toggleUserActive(UUID userId) {
    User user = validateAndGetUser(userId);
    user.setActive(!user.isActive());
    userRepository.save(user);
  }

  public Page<UserResponse> getUsers(UserType userType, Pageable pageable) {
    Page<User> users = userType != null ? userRepository.findByUserType(userType, pageable)
        : userRepository.findAll(pageable);
    return users.map(this::toUserResponse);
  }

  public UserResponse getUserById(UUID userId) {
    return toUserResponse(validateAndGetUser(userId));
  }

  private UserResponse toUserResponse(User user) {
    UserResponse response = new UserResponse();
    response.setId(user.getId());
    response.setName(user.getName());
    response.setEmail(user.getEmail());
    response.setPhone(user.getPhone());
    response.setUserType(user.getUserType());
    response.setActive(user.isActive());
    response.setCreatedAt(user.getCreatedAt());
    response.setUpdatedAt(user.getUpdatedAt());
    return response;
  }
}