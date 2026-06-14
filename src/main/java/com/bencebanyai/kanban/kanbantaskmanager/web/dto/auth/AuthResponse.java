package com.bencebanyai.kanban.kanbantaskmanager.web.dto.auth;

public record AuthResponse(
        String token,
        String email,
        String displayName
) {
}
