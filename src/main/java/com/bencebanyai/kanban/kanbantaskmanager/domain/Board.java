package com.bencebanyai.kanban.kanbantaskmanager.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import lombok.*;

/** Board entity representing a Kanban board owned by a user. */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "boards")
public class Board {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Setter(AccessLevel.NONE)
  private Long id;

  @Column(name = "name", nullable = false, length = 100)
  private String name;

  @Column(name = "description")
  private String description;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  @Column(name = "is_archived", nullable = false)
  private boolean isArchived;

  @Version
  @Column(name = "version")
  private Long version;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "owner_id", nullable = false)
  private User owner;

  @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<BoardColumn> columns = new HashSet<>();

  public void addColumn(BoardColumn column) {
    columns.add(column);
    column.setBoard(this);
  }

  public void removeColumn(BoardColumn column) {
    columns.remove(column);
    column.setBoard(null);
  }
}
