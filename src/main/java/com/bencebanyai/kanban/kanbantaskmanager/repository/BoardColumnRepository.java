package com.bencebanyai.kanban.kanbantaskmanager.repository;

import com.bencebanyai.kanban.kanbantaskmanager.domain.BoardColumn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardColumnRepository extends JpaRepository<BoardColumn, Long> {}
