package com.bencebanyai.kanban.kanbantaskmanager.repository;

import com.bencebanyai.kanban.kanbantaskmanager.domain.Label;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LabelRepository extends JpaRepository<Label, Long> {}
