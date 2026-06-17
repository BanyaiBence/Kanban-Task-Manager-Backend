package com.bencebanyai.kanban.kanbantaskmanager.security;

import com.bencebanyai.kanban.kanbantaskmanager.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("boardSecurity")
@RequiredArgsConstructor
public class BoardSecurity {

  private final BoardRepository boardRepository;

  public boolean isOwner(Long boardId, String userEmail) {
    if (boardId == null || userEmail == null) {
      return false;
    }
    return boardRepository.existsByIdAndOwnerEmail(boardId, userEmail);
  }
}
