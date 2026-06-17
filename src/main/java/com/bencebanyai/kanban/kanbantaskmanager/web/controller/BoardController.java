package com.bencebanyai.kanban.kanbantaskmanager.web.controller;

import com.bencebanyai.kanban.kanbantaskmanager.service.BoardService;
import com.bencebanyai.kanban.kanbantaskmanager.web.dto.board.BoardDto;
import com.bencebanyai.kanban.kanbantaskmanager.web.dto.board.CreateBoardRequest;
import com.bencebanyai.kanban.kanbantaskmanager.web.dto.board.UpdateBoardRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

  private final BoardService boardService;

  @PostMapping
  public ResponseEntity<BoardDto> createBoard(
      @Valid @RequestBody CreateBoardRequest request,
      @AuthenticationPrincipal UserDetails userDetails) {
    BoardDto createdBoard = boardService.createBoard(request, userDetails.getUsername());
    return ResponseEntity.status(HttpStatus.CREATED).body(createdBoard);
  }

  @GetMapping
  public ResponseEntity<List<BoardDto>> getUserBoards(
      @AuthenticationPrincipal UserDetails userDetails) {
    List<BoardDto> boards = boardService.getUserBoards(userDetails.getUsername());
    return ResponseEntity.ok(boards);
  }

  @GetMapping("/{boardId}")
  @PreAuthorize("@boardSecurity.isOwner(#boardId, authentication.name)")
  public ResponseEntity<BoardDto> getBoardById(@PathVariable Long boardId) {
    BoardDto board = boardService.getBoardById(boardId);
    return ResponseEntity.ok(board);
  }

  @PutMapping("/{boardId}")
  @PreAuthorize("@boardSecurity.isOwner(#boardId, authentication.name)")
  public ResponseEntity<BoardDto> updateBoard(
      @PathVariable Long boardId, @Valid @RequestBody UpdateBoardRequest request) {
    BoardDto updatedBoard = boardService.updateBoard(boardId, request);
    return ResponseEntity.ok(updatedBoard);
  }

  @DeleteMapping("/{boardId}")
  @PreAuthorize("@boardSecurity.isOwner(#boardId, authentication.name)")
  public ResponseEntity<Void> archiveBoard(@PathVariable Long boardId) {
    boardService.archiveBoard(boardId);
    return ResponseEntity.noContent().build();
  }
}
