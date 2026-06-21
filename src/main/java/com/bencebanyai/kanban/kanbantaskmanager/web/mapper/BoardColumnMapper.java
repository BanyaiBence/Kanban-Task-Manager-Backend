package com.bencebanyai.kanban.kanbantaskmanager.web.mapper;

import com.bencebanyai.kanban.kanbantaskmanager.domain.BoardColumn;
import com.bencebanyai.kanban.kanbantaskmanager.web.dto.column.BoardColumnResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BoardColumnMapper {

    public BoardColumnResponse boardColumnToResponse(BoardColumn boardColumn);

}
