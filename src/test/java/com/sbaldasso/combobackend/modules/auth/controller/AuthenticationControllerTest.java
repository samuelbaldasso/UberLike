package com.sbaldasso.combobackend.modules.auth.controller;

import com.sbaldasso.combobackend.modules.auth.dto.AuthenticationRequest;
import com.sbaldasso.combobackend.modules.auth.dto.AuthenticationResponse;
import com.sbaldasso.combobackend.modules.auth.service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthenticationControllerTest {
  @Mock
  private AuthenticationService authenticationService;
  @InjectMocks
  private AuthenticationController authenticationController;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void authenticate_returnsAuthenticationResponse() {
    AuthenticationRequest request = new AuthenticationRequest();
    AuthenticationResponse response = AuthenticationResponse.builder().token("token").user(null).build();
    when(authenticationService.authenticate(request)).thenReturn(response);
    ResponseEntity<AuthenticationResponse> result = authenticationController.authenticate(request);
    assertEquals(response, result.getBody());
    verify(authenticationService).authenticate(request);
  }
}
