package com.bencebanyai.kanban.kanbantaskmanager.web.dto.board;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateBoardRequest(
    @NotBlank(message = "Board name cannot be blank")
        @Size(max = 100, message = "Board name must not exceed 100 characters")
        String name,
    String description,
    boolean isArchived) {}
