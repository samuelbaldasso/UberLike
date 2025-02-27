package com.sbaldasso.combobackend.service;

import com.sbaldasso.combobackend.dtos.AuthenticationRequest;
import com.sbaldasso.combobackend.dtos.AuthenticationResponse;
import com.sbaldasso.combobackend.dtos.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final UserService userService;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;
  private final UserDetailsServiceCustom userDetailsService;

  public AuthenticationResponse register(UserDto request) {
    var user = userService.createUser(request);
    var userDetails = userDetailsService.loadUserByUsername(user.getEmail());
    var jwtToken = jwtService.generateToken(userDetails);
    return AuthenticationResponse.builder()
        .token(jwtToken)
        .build();
  }

  public AuthenticationResponse authenticate(AuthenticationRequest request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.getEmail(),
            request.getPassword()));
    UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
    var jwtToken = jwtService.generateToken(userDetails);
    return AuthenticationResponse.builder()
        .token(jwtToken)
        .build();
  }
}