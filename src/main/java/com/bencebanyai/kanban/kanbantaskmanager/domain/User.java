package com.bencebanyai.kanban.kanbantaskmanager.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import lombok.*;

/** User entity representing a registered user in the system. */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Setter(AccessLevel.NONE)
  private Long id;

  @Column(name = "email", nullable = false, unique = true)
  private String email;

  @Column(name = "password_hash", nullable = false)
  private String passwordHash;

  @Column(name = "display_name", nullable = false)
  private String displayName;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @Builder.Default
  @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<Board> boards = new HashSet<>();

  @Builder.Default
  @OneToMany(mappedBy = "assignee")
  private Set<Task> assignedTasks = new HashSet<>();

  @Builder.Default
  @OneToMany(mappedBy = "author")
  private Set<Comment> comments = new HashSet<>();

  public void addBoard(Board board) {
    boards.add(board);
    board.setOwner(this);
  }

  public void removeBoard(Board board) {
    boards.remove(board);
    board.setOwner(null);
  }

  public void addAssignedTask(Task task) {
    assignedTasks.add(task);
    task.setAssignee(this);
  }

  public void removeAssignedTask(Task task) {
    assignedTasks.remove(task);
    task.setAssignee(null);
  }

  public void addComment(Comment comment) {
    comments.add(comment);
    comment.setAuthor(this);
  }

  public void removeComment(Comment comment) {
    comments.remove(comment);
    comment.setAuthor(null);
  }
}
