package com.bencebanyai.kanban.kanbantaskmanager.repository;

import com.bencebanyai.kanban.kanbantaskmanager.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
}
