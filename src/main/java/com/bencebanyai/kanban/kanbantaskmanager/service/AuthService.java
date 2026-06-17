package com.bencebanyai.kanban.kanbantaskmanager.service;

import com.bencebanyai.kanban.kanbantaskmanager.domain.User;
import com.bencebanyai.kanban.kanbantaskmanager.repository.UserRepository;
import com.bencebanyai.kanban.kanbantaskmanager.security.JwtUtils;
import com.bencebanyai.kanban.kanbantaskmanager.web.dto.auth.AuthResponse;
import com.bencebanyai.kanban.kanbantaskmanager.web.dto.auth.LoginRequest;
import com.bencebanyai.kanban.kanbantaskmanager.web.dto.auth.RegisterRequest;
import com.bencebanyai.kanban.kanbantaskmanager.web.exception.UserAlreadyTakenException;
import jakarta.transaction.Transactional;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtUtils jwtUtils;

  /** Handles new user registration. */
  @Transactional
  public AuthResponse register(RegisterRequest request) {
    if (userRepository.existsByEmail(request.email())) {
      throw new UserAlreadyTakenException("A user with this email already exists.");
    }

    User user =
        User.builder()
            .email(request.email())
            .displayName(request.displayName())
            .passwordHash(passwordEncoder.encode(request.password()))
            .createdAt(Instant.now())
            .build();

    userRepository.save(user);

    String token = jwtUtils.generateToken(user.getEmail());

    return new AuthResponse(token, user.getEmail(), user.getDisplayName());
  }

  /** Handles user login. */
  public AuthResponse login(LoginRequest request) {
    Authentication auth =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.email(), request.password()));

    String email = auth.getName();
    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found after authentication"));

    String token = jwtUtils.generateToken(user.getEmail());

    return new AuthResponse(token, user.getEmail(), user.getDisplayName());
  }
}
