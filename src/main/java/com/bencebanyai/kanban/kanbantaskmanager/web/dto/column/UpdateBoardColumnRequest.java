package com.bencebanyai.kanban.kanbantaskmanager.web.dto.column;

import jakarta.validation.constraints.Size;

public record UpdateBoardColumnRequest(
        @Size(min = 1, max = 50, message = "Column name must be between 1 and 50 characters")
        String name,
        String prevPosition,
        String nextPosition
) {
}
