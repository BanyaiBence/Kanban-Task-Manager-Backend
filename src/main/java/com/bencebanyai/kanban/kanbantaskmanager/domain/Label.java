package com.bencebanyai.kanban.kanbantaskmanager.domain;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import lombok.*;

/** Label entity representing a label that can be attached to tasks. */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "labels")
public class Label {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Setter(AccessLevel.NONE)
  private Long id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "color", nullable = false)
  private String color;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "board_id", nullable = false)
  private Board board;

  @ManyToMany(mappedBy = "labels")
  private Set<Task> tasks = new HashSet<>();
}
