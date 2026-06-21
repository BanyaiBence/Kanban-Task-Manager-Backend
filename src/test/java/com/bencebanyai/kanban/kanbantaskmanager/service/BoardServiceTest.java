package com.bencebanyai.kanban.kanbantaskmanager.service;

import com.bencebanyai.kanban.kanbantaskmanager.domain.Board;
import com.bencebanyai.kanban.kanbantaskmanager.domain.User;
import com.bencebanyai.kanban.kanbantaskmanager.repository.BoardRepository;
import com.bencebanyai.kanban.kanbantaskmanager.repository.UserRepository;
import com.bencebanyai.kanban.kanbantaskmanager.web.dto.board.BoardResponse;
import com.bencebanyai.kanban.kanbantaskmanager.web.dto.board.CreateBoardRequest;
import com.bencebanyai.kanban.kanbantaskmanager.web.dto.board.UpdateBoardRequest;
import com.bencebanyai.kanban.kanbantaskmanager.web.mapper.BoardMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BoardServiceTest {

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BoardMapper boardMapper;

    @InjectMocks
    private BoardService boardService;

    private User owner;
    private Board board;
    private BoardResponse boardResponse;

    @BeforeEach
    void setUp() {
        owner =
                User.builder()
                        .email("test@example.com")
                        .passwordHash("hash")
                        .displayName("Test User")
                        .createdAt(Instant.now())
                        .build();

        board =
                new Board(
                        1L,
                        "Test Board",
                        "Description",
                        Instant.now(),
                        Instant.now(),
                        false,
                        0L,
                        owner,
                        new HashSet<>());

        boardResponse =
                new BoardResponse(1L, "Test Board", "Description", Instant.now(), Instant.now(), false, 1L);
    }

    @Test
    void createBoard_WithValidRequest_ShouldReturnBoardDto() {
        CreateBoardRequest request = new CreateBoardRequest("Test Board", "Description");
        String userEmail = "test@example.com";

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(owner));
        when(boardRepository.save(any(Board.class))).thenReturn(board);
        when(boardMapper.boardToResponse(any(Board.class))).thenReturn(boardResponse);

        BoardResponse result = boardService.createBoard(request, userEmail);

        assertNotNull(result);
        assertEquals("Test Board", result.name());
        verify(userRepository).findByEmail(userEmail);
        verify(boardRepository).save(any(Board.class));
        verify(boardMapper).boardToResponse(board);
    }

    @Test
    void createBoard_WithUserNotFound_ShouldThrowException() {
        CreateBoardRequest request = new CreateBoardRequest("Test Board", "Description");
        String userEmail = "notfound@example.com";

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> boardService.createBoard(request, userEmail));
        verify(boardRepository, never()).save(any(Board.class));
    }

    @Test
    void getBoardById_WithExistingBoard_ShouldReturnBoardDto() {
        when(boardRepository.findById(1L)).thenReturn(Optional.of(board));
        when(boardMapper.boardToResponse(board)).thenReturn(boardResponse);

        BoardResponse result = boardService.getBoardById(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
        verify(boardRepository).findById(1L);
        verify(boardMapper).boardToResponse(board);
    }

    @Test
    void getBoardById_WithNonExistingBoard_ShouldThrowException() {
        when(boardRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> boardService.getBoardById(99L));
        verify(boardMapper, never()).boardToResponse(any());
    }

    @Test
    void updateBoard_WithValidRequest_ShouldReturnUpdatedBoardDto() {
        UpdateBoardRequest request = new UpdateBoardRequest("Updated Board", "Updated Desc", false);

        when(boardRepository.findById(1L)).thenReturn(Optional.of(board));
        when(boardRepository.save(any(Board.class))).thenReturn(board);
        when(boardMapper.boardToResponse(any(Board.class))).thenReturn(boardResponse);

        BoardResponse result = boardService.updateBoard(1L, request);

        assertNotNull(result);
        verify(boardRepository).findById(1L);
        verify(boardRepository)
                .save(
                        argThat(
                                b ->
                                        "Updated Board".equals(b.getName())
                                                && "Updated Desc".equals(b.getDescription())));
        verify(boardMapper).boardToResponse(board);
    }

    @Test
    void archiveBoard_WithExistingBoard_ShouldSetArchivedFlag() {
        when(boardRepository.findById(1L)).thenReturn(Optional.of(board));
        when(boardRepository.save(any(Board.class))).thenReturn(board);

        boardService.archiveBoard(1L);

        verify(boardRepository).findById(1L);
        verify(boardRepository).save(argThat(Board::isArchived));
    }

    @Test
    void getUserBoards_ShouldReturnListOfBoardDtos() {
        String userEmail = "test@example.com";
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(owner));
        when(boardRepository.findByOwnerIdAndIsArchivedFalse(any())).thenReturn(List.of(board));
        when(boardMapper.boardToResponse(board)).thenReturn(boardResponse);

        List<BoardResponse> results = boardService.getUserBoards(userEmail);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("Test Board", results.get(0).name());
        verify(boardRepository).findByOwnerIdAndIsArchivedFalse(any());
    }
}
