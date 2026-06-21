package com.bencebanyai.kanban.kanbantaskmanager.web.dto.column;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateBoardColumnRequest(
        @NotBlank(message = "Column name is required")
        @Size(min = 1, max = 50, message = "Column name must be between 1 and 50 characters")
        String name
) {
}
