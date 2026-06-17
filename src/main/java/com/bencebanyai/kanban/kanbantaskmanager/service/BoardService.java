package com.bencebanyai.kanban.kanbantaskmanager.service;

import com.bencebanyai.kanban.kanbantaskmanager.domain.Board;
import com.bencebanyai.kanban.kanbantaskmanager.domain.User;
import com.bencebanyai.kanban.kanbantaskmanager.repository.BoardRepository;
import com.bencebanyai.kanban.kanbantaskmanager.repository.UserRepository;
import com.bencebanyai.kanban.kanbantaskmanager.web.dto.board.BoardDto;
import com.bencebanyai.kanban.kanbantaskmanager.web.dto.board.CreateBoardRequest;
import com.bencebanyai.kanban.kanbantaskmanager.web.dto.board.UpdateBoardRequest;
import com.bencebanyai.kanban.kanbantaskmanager.web.mapper.BoardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final BoardMapper boardMapper;

    @Transactional
    public BoardDto createBoard(CreateBoardRequest request, String userEmail) {
        User owner =
                userRepository
                        .findByEmail(userEmail)
                        .orElseThrow(
                                () ->
                                        new org.springframework.web.server.ResponseStatusException(
                                                org.springframework.http.HttpStatus.NOT_FOUND, "User not found"));

        Board board =
                new Board(
                        null,
                        request.name(),
                        request.description(),
                        java.time.Instant.now(),
                        java.time.Instant.now(),
                        false,
                        0L,
                        owner,
                        new java.util.HashSet<>());

        Board savedBoard = boardRepository.save(board);
        return boardMapper.boardToDto(savedBoard);
    }

    @Transactional(readOnly = true)
    public BoardDto getBoardById(Long id) {
        Board board =
                boardRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new org.springframework.web.server.ResponseStatusException(
                                                org.springframework.http.HttpStatus.NOT_FOUND, "Board not found"));
        return boardMapper.boardToDto(board);
    }

    @Transactional
    public BoardDto updateBoard(Long id, UpdateBoardRequest request) {
        Board board =
                boardRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new org.springframework.web.server.ResponseStatusException(
                                                org.springframework.http.HttpStatus.NOT_FOUND, "Board not found"));

        board.setName(request.name());
        board.setDescription(request.description());
        board.setArchived(request.isArchived());
        board.setUpdatedAt(java.time.Instant.now());

        Board updatedBoard = boardRepository.save(board);
        return boardMapper.boardToDto(updatedBoard);
    }

    @Transactional
    public void archiveBoard(Long id) {
        Board board =
                boardRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new org.springframework.web.server.ResponseStatusException(
                                                org.springframework.http.HttpStatus.NOT_FOUND, "Board not found"));

        board.setArchived(true);
        board.setUpdatedAt(java.time.Instant.now());
        boardRepository.save(board);
    }

    @Transactional(readOnly = true)
    public List<BoardDto> getUserBoards(String userEmail) {
        User owner =
                userRepository
                        .findByEmail(userEmail)
                        .orElseThrow(
                                () ->
                                        new org.springframework.web.server.ResponseStatusException(
                                                org.springframework.http.HttpStatus.NOT_FOUND, "User not found"));

        return boardRepository.findByOwnerIdAndIsArchivedFalse(owner.getId()).stream()
                .map(boardMapper::boardToDto)
                .toList();
    }
}
