package com.bencebanyai.kanban.kanbantaskmanager.web.controller;

import com.bencebanyai.kanban.kanbantaskmanager.service.AuthService;
import com.bencebanyai.kanban.kanbantaskmanager.web.dto.auth.AuthResponse;
import com.bencebanyai.kanban.kanbantaskmanager.web.dto.auth.LoginRequest;
import com.bencebanyai.kanban.kanbantaskmanager.web.dto.auth.RegisterRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  /** POST /api/auth/register Registers a new user and returns a JWT token. */
  @PostMapping("/register")
  public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {

    AuthResponse response = authService.register(request);

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  /** POST /api/auth/login Authenticates existing credentials and returns a JWT token. */
  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {

    AuthResponse response = authService.login(request);

    return ResponseEntity.ok(response);
  }
}
