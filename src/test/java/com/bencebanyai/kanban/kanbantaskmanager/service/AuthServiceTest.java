package com.bencebanyai.kanban.kanbantaskmanager.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.bencebanyai.kanban.kanbantaskmanager.domain.User;
import com.bencebanyai.kanban.kanbantaskmanager.repository.UserRepository;
import com.bencebanyai.kanban.kanbantaskmanager.security.JwtUtils;
import com.bencebanyai.kanban.kanbantaskmanager.web.dto.auth.AuthResponse;
import com.bencebanyai.kanban.kanbantaskmanager.web.dto.auth.LoginRequest;
import com.bencebanyai.kanban.kanbantaskmanager.web.dto.auth.RegisterRequest;
import com.bencebanyai.kanban.kanbantaskmanager.web.exception.UserAlreadyTakenException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  @Mock private UserRepository userRepository;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private AuthenticationManager authenticationManager;
  @Mock private JwtUtils jwtUtils;

  @InjectMocks private AuthService authService;

  @Test
  void register_WhenEmailIsUnique_ShouldSaveUserAndReturnToken() {
    RegisterRequest request = new RegisterRequest("new@test.com", "password123", "New User");

    when(userRepository.existsByEmail(request.email())).thenReturn(false);
    when(passwordEncoder.encode(request.password())).thenReturn("hashedPassword");
    when(jwtUtils.generateToken(request.email())).thenReturn("mockJwtToken");

    AuthResponse response = authService.register(request);

    assertNotNull(response);
    assertEquals("mockJwtToken", response.token());
    assertEquals("new@test.com", response.email());

    verify(userRepository, times(1)).save(any(User.class));
  }

  @Test
  void register_WhenEmailExists_ShouldThrowException() {
    RegisterRequest request = new RegisterRequest("existing@test.com", "password123", "User");

    when(userRepository.existsByEmail(request.email())).thenReturn(true);

    assertThrows(
        UserAlreadyTakenException.class,
        () -> {
          authService.register(request);
        });

    verify(userRepository, never()).save(any(User.class));
    verify(passwordEncoder, never()).encode(anyString());
  }

  @Test
  void login_WithValidCredentials_ShouldReturnAuthResponse() {
    LoginRequest request = new LoginRequest("valid@test.com", "correctPassword");

    Authentication mockAuthentication = mock(Authentication.class);
    when(mockAuthentication.getName()).thenReturn(request.email());

    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenReturn(mockAuthentication);

    User mockUser = User.builder().email(request.email()).displayName("Valid User").build();
    when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(mockUser));
    when(jwtUtils.generateToken(request.email())).thenReturn("mockedJwtToken");

    AuthResponse response = authService.login(request);

    assertNotNull(response);
    assertEquals("mockedJwtToken", response.token());
    assertEquals("valid@test.com", response.email());
    assertEquals("Valid User", response.displayName());

    verify(authenticationManager, times(1))
        .authenticate(any(UsernamePasswordAuthenticationToken.class));
  }

  @Test
  void login_WithInvalidCredentials_ShouldThrowBadCredentialsException() {
    LoginRequest request = new LoginRequest("wrong@test.com", "wrongPassword");

    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenThrow(new BadCredentialsException("Bad credentials"));

    assertThrows(
        BadCredentialsException.class,
        () -> {
          authService.login(request);
        });

    verify(userRepository, never()).findByEmail(anyString());
    verify(jwtUtils, never()).generateToken(anyString());
  }
}
