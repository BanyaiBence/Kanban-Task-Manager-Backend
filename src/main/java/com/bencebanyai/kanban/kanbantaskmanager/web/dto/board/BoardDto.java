package com.bencebanyai.kanban.kanbantaskmanager.web.dto.board;

import java.time.Instant;

public record BoardDto(
    Long id,
    String name,
    String description,
    Instant createdAt,
    Instant updatedAt,
    boolean isArchived,
    Long ownerId) {}
