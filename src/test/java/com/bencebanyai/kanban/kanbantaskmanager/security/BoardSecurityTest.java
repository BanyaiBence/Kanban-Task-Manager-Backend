package com.bencebanyai.kanban.kanbantaskmanager.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bencebanyai.kanban.kanbantaskmanager.repository.BoardRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BoardSecurityTest {

  @Mock private BoardRepository boardRepository;

  @InjectMocks private BoardSecurity boardSecurity;

  @Test
  void isOwner_WithValidOwner_ShouldReturnTrue() {
    // Arrange
    Long boardId = 1L;
    String email = "test@example.com";
    when(boardRepository.existsByIdAndOwnerEmail(boardId, email)).thenReturn(true);

    // Act
    boolean result = boardSecurity.isOwner(boardId, email);

    // Assert
    assertTrue(result);
    verify(boardRepository).existsByIdAndOwnerEmail(boardId, email);
  }

  @Test
  void isOwner_WithInvalidOwner_ShouldReturnFalse() {
    // Arrange
    Long boardId = 1L;
    String email = "wrong@example.com";
    when(boardRepository.existsByIdAndOwnerEmail(boardId, email)).thenReturn(false);

    // Act
    boolean result = boardSecurity.isOwner(boardId, email);

    // Assert
    assertFalse(result);
    verify(boardRepository).existsByIdAndOwnerEmail(boardId, email);
  }

  @Test
  void isOwner_WithNullBoardId_ShouldReturnFalse() {
    assertFalse(boardSecurity.isOwner(null, "test@example.com"));
  }

  @Test
  void isOwner_WithNullEmail_ShouldReturnFalse() {
    assertFalse(boardSecurity.isOwner(1L, null));
  }
}
