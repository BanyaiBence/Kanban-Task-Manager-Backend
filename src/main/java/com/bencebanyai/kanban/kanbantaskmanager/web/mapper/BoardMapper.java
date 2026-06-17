package com.bencebanyai.kanban.kanbantaskmanager.web.mapper;

import com.bencebanyai.kanban.kanbantaskmanager.domain.Board;
import com.bencebanyai.kanban.kanbantaskmanager.web.dto.board.BoardDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BoardMapper {
    public BoardDto boardToDto(Board board);

    public Board dtoToBoard(BoardDto boardDto);
}
