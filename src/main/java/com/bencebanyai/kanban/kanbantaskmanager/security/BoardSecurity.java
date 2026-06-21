package com.bencebanyai.kanban.kanbantaskmanager.security;

import com.bencebanyai.kanban.kanbantaskmanager.repository.BoardColumnRepository;
import com.bencebanyai.kanban.kanbantaskmanager.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("boardSecurity")
@RequiredArgsConstructor
public class BoardSecurity {

    private final BoardRepository boardRepository;
    private final BoardColumnRepository boardColumnRepository;

    public boolean isOwner(Long boardId, String userEmail) {
        if (boardId == null || userEmail == null) {
            return false;
        }
        return boardRepository.existsByIdAndOwnerEmail(boardId, userEmail);
    }

    public boolean isColumnOwner(Long columnId) {
        if (columnId == null) {
            return false;
        }
        String currentUserEmail = getCurrentUserEmail();
        if (currentUserEmail == null) {
            return false;
        }

        return boardColumnRepository.findById(columnId)
                .map(column -> column.getBoard().getOwner().getEmail().equals(currentUserEmail))
                .orElse(false);

    }

    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        return authentication.getName();
    }
}
