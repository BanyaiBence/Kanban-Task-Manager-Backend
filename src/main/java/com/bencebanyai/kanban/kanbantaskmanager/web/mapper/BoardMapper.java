package com.bencebanyai.kanban.kanbantaskmanager.web.mapper;

import com.bencebanyai.kanban.kanbantaskmanager.domain.Board;
import com.bencebanyai.kanban.kanbantaskmanager.web.dto.board.BoardDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BoardMapper {
    @Mapping(target = "isArchived", source = "archived")
    @Mapping(target = "ownerId", source = "owner.id")
    public BoardDto boardToDto(Board board);

    @Mapping(target = "archived", source = "isArchived")
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "columns", ignore = true)
    public Board dtoToBoard(BoardDto boardDto);
}
