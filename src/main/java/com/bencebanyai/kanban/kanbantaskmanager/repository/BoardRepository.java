package com.bencebanyai.kanban.kanbantaskmanager.repository;

import com.bencebanyai.kanban.kanbantaskmanager.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
}
