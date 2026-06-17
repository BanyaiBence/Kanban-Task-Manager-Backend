package com.bencebanyai.kanban.kanbantaskmanager.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import lombok.*;

/** Task entity representing a task in a Kanban column. */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(
    name = "tasks",
    indexes = @Index(name = "idx_tasks_column_position", columnList = "column_id,position"))
public class Task {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Setter(AccessLevel.NONE)
  private Long id;

  @Column(name = "title", nullable = false)
  private String title;

  @Column(name = "description")
  private String description;

  @Column(name = "position", nullable = false)
  private String position;

  @Column(name = "due_date")
  private Instant dueDate;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  @Column(name = "is_archived", nullable = false)
  private boolean archived;

  @Version
  @Column(name = "version")
  private Long version;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "column_id", nullable = false)
  private BoardColumn column;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "assignee_id")
  private User assignee;

  @ManyToMany
  @JoinTable(
      name = "task_labels",
      joinColumns = @JoinColumn(name = "task_id"),
      inverseJoinColumns = @JoinColumn(name = "label_id"))
  private Set<Label> labels = new HashSet<>();

  @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<Comment> comments = new HashSet<>();

  public void addLabel(Label label) {
    labels.add(label);
    label.getTasks().add(this);
  }

  public void removeLabel(Label label) {
    labels.remove(label);
    label.getTasks().remove(this);
  }

  public void addComment(Comment comment) {
    comments.add(comment);
    comment.setTask(this);
  }

  public void removeComment(Comment comment) {
    comments.remove(comment);
    comment.setTask(null);
  }
}
