package com.sbaldasso.combobackend.modules.auth.service;

import com.sbaldasso.combobackend.modules.auth.dto.AuthenticationRequest;
import com.sbaldasso.combobackend.modules.auth.dto.AuthenticationResponse;
import com.sbaldasso.combobackend.modules.user.domain.User;
import com.sbaldasso.combobackend.modules.user.repository.UserRepository;
import com.sbaldasso.combobackend.modules.user.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest {
  @Mock
  private UserRepository userRepository;
  @Mock
  private UserServiceImpl userService;
  @Mock
  private JwtService jwtService;
  @Mock
  private AuthenticationManager authenticationManager;
  @InjectMocks
  private AuthenticationService authenticationService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void authenticate_throwsIfUserNotFound() {
    AuthenticationRequest request = new AuthenticationRequest();
    request.setEmail("test@test.com");
    request.setPassword("123");
    Authentication authentication = mock(Authentication.class);
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
    when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.empty());
    assertThrows(IllegalArgumentException.class, () -> authenticationService.authenticate(request));
  }
}
