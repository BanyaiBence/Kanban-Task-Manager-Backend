package com.bencebanyai.kanban.kanbantaskmanager.web.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @NotBlank(message = "Email cannot be blank") @Email String email,
    @NotBlank(message = "Password cannot be blank")
        @Size(
            min = 8,
            max = 64,
            message = "Password should have a length between 8 and 64 characters")
        String password,
    @NotBlank(message = "Display name cannot be blank")
        @Size(
            min = 2,
            max = 50,
            message = "Display name should have a length between 2 and 50 characters")
        String displayName) {}
