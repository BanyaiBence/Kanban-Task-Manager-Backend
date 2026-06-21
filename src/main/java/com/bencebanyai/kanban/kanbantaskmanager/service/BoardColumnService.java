package com.bencebanyai.kanban.kanbantaskmanager.service;

import com.bencebanyai.kanban.kanbantaskmanager.domain.Board;
import com.bencebanyai.kanban.kanbantaskmanager.domain.BoardColumn;
import com.bencebanyai.kanban.kanbantaskmanager.repository.BoardColumnRepository;
import com.bencebanyai.kanban.kanbantaskmanager.repository.BoardRepository;
import com.bencebanyai.kanban.kanbantaskmanager.utils.LexoRankUtils;
import com.bencebanyai.kanban.kanbantaskmanager.web.dto.column.BoardColumnResponse;
import com.bencebanyai.kanban.kanbantaskmanager.web.mapper.BoardColumnMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BoardColumnService {

    private final BoardColumnRepository boardColumnRepository;
    private final BoardRepository boardRepository;
    private final LexoRankUtils lexoRankUtils;
    private final BoardColumnMapper boardColumnMapper;

    public BoardColumnResponse createColumn(Long boardId, String name) {
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new EntityNotFoundException("Board not found with id: " + boardId));

        BoardColumn lastColumn = boardColumnRepository.findFirstByBoardIdOrderByPositionDesc(boardId).orElse(null);

        String prevPosition = lastColumn != null ? lastColumn.getPosition() : null;

        String newPosition = lexoRankUtils.getMidpoint(prevPosition, null);

        BoardColumn column = BoardColumn.builder().board(board).name(name).position(newPosition).build();
        BoardColumn savedColumn = boardColumnRepository.save(column);

        return boardColumnMapper.boardColumnToResponse(savedColumn);
    }

    public BoardColumnResponse renameColumn(Long boardId, String newName) {
        BoardColumn column = getColumnOrThrow(boardId);
        column.setName(newName);
        BoardColumn savedColumn = boardColumnRepository.save(column);
        return boardColumnMapper.boardColumnToResponse(savedColumn);
    }

    public BoardColumnResponse reorderColumn(Long columnId, String prevPosition, String nextPosition) {
        BoardColumn column = getColumnOrThrow(columnId);
        String newPosition = lexoRankUtils.getMidpoint(prevPosition, nextPosition);
        column.setPosition(newPosition);
        BoardColumn savedColumn = boardColumnRepository.save(column);

        return boardColumnMapper.boardColumnToResponse(savedColumn);
    }

    public void deleteColumn(Long columnId) {
        BoardColumn column = getColumnOrThrow(columnId);
        boardColumnRepository.delete(column);
    }

    private BoardColumn getColumnOrThrow(Long columnId) {
        return boardColumnRepository.findById(columnId)
                .orElseThrow(() -> new EntityNotFoundException("Column not found with id: " + columnId));
    }
}
