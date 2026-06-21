package com.bencebanyai.kanban.kanbantaskmanager.web.controller;

import com.bencebanyai.kanban.kanbantaskmanager.service.BoardColumnService;
import com.bencebanyai.kanban.kanbantaskmanager.web.dto.column.BoardColumnResponse;
import com.bencebanyai.kanban.kanbantaskmanager.web.dto.column.CreateBoardColumnRequest;
import com.bencebanyai.kanban.kanbantaskmanager.web.dto.column.UpdateBoardColumnRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ColumnController {

    private final BoardColumnService boardColumnService;

    @PostMapping("/api/board/{boardId}/columns")
    @PreAuthorize("@boardSecurity.isColumnOwner(#boardId)")
    public ResponseEntity<BoardColumnResponse> createColumn(
            @PathVariable Long boardId,
            @Valid @RequestBody CreateBoardColumnRequest request
    ) {
        BoardColumnResponse createdColumn = boardColumnService.createColumn(boardId, request.name());

        return ResponseEntity.status(HttpStatus.CREATED).body(createdColumn);
    }

    @PatchMapping("/api/columns/{columnId}")
    @PreAuthorize("@boardSecurity.isColumnOwner(#columnId)")
    public ResponseEntity<BoardColumnResponse> updateColumn(
            @PathVariable Long columnId,
            @Valid @RequestBody UpdateBoardColumnRequest request) {

        BoardColumnResponse updatedColumn;

        // If prev/next positions are provided, this is a drag-and-drop reorder event
        if (request.prevPosition() != null || request.nextPosition() != null) {
            updatedColumn = boardColumnService.reorderColumn(columnId, request.prevPosition(), request.nextPosition());
        }
        // Otherwise, it's a simple rename event
        else if (request.name() != null) {
            updatedColumn = boardColumnService.renameColumn(columnId, request.name());
        } else {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(updatedColumn);
    }

    @DeleteMapping("/api/columns/{columnId}")
    @PreAuthorize("@boardSecurity.isColumnOwner(#columnId)")
    public ResponseEntity<Void> deleteColumn(@PathVariable Long columnId) {
        boardColumnService.deleteColumn(columnId);
        return ResponseEntity.noContent().build();
    }
}
