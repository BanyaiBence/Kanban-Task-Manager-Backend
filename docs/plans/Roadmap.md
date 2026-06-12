# Backend Development Roadmap: Kanban REST API

## Phase 1: Infrastructure & Scaffolding
* Initialize Spring Boot project (Java 21, Spring Web, Spring Data JPA, Validation, PostgreSQL Driver, Flyway, Spring Security).
* Create `docker-compose.yml` for local PostgreSQL instance.
* Configure `application.yml`:
    * Database connection properties.
    * Set `spring.jpa.hibernate.ddl-auto=validate`.
    * Enable Flyway (`spring.flyway.enabled=true`).
* Implement global `@ControllerAdvice` for standardized JSON exception handling (validation errors, resource not found, unauthorized).
* Initialize empty Flyway migration file: `src/main/resources/db/migration/V1__init_schema.sql`.

## Phase 2: Domain Modeling & Database Migrations
* Define standard SQL DDL statements in `V1__init_schema.sql` for tables: `users`, `boards`, `columns`, `tasks`, `labels`, `task_labels`, `comments`.
* Include `version BIGINT` columns on `boards`, `columns`, and `tasks` for optimistic locking.
* Implement JPA `@Entity` classes.
* Map entity relations (`@OneToMany`, `@ManyToOne`, `@ManyToMany`).
* Annotate locking fields with `@Version`.
* Implement base Spring Data JPA `Repository` interfaces for all entities to verify context load.

## Phase 3: Security & Authentication
* Implement `UserDetailsService` to load users by email address.
* Implement JWT utility component (generate, sign, parse, and validate JSON Web Tokens).
* Implement custom `OncePerRequestFilter` to extract JWT from `Authorization` header, execute validation, and populate `SecurityContext`.
* Configure `SecurityFilterChain`:
    * Set session creation policy to `STATELESS`.
    * Permit unauthenticated access to `/api/auth/**`.
    * Require authentication for all other requests.
* Implement `AuthController` and `AuthService`:
    * Registration endpoint (Hash passwords using `BCryptPasswordEncoder`).
    * Login endpoint (Return JWT upon successful authentication).

## Phase 4: Board Management & Granular Authorization
* Define Request/Response DTOs and mappers for `Board` operations.
* Implement `BoardService` for CRUD operations. Enforce soft delete mechanisms via `is_archived` flag updates.
* Implement `BoardSecurity` authorization bean containing `isOwner(Long boardId, String userEmail)` logic.
* Implement `BoardController`. Secure endpoints using `@PreAuthorize("@boardSecurity.isOwner(#boardId)")`.

## Phase 5: Columns & Lexicographical Ordering
* Implement `LexoRankUtils` component to handle string-based position generation and midpoint string calculation.
* Implement unit tests for `LexoRankUtils` to verify exact midpoint calculations and boundary conditions.
* Implement `ColumnService`. Utilize `LexoRankUtils` to assign trailing positions upon column creation.
* Implement `ColumnController`. Apply `@PreAuthorize` board ownership checks to column endpoints.

## Phase 6: Task Management & Advanced Querying
* Implement `TaskService` for standard CRUD operations and entity assignment.
* Implement $O(1)$ task reordering logic in `TaskService`. Calculate new lexicographical position strings and explicitly handle/log `ObjectOptimisticLockingFailureException`.
* Implement dynamic queries in `TaskRepository` using JPA Specifications or `@Query` to filter by `columnId`, `assigneeId`, or text search.
* Implement `TaskController`. Map filtered results to `Page<TaskDto>` to support client-side pagination.

## Phase 7: Labels & Comments
* Implement `LabelService` and `LabelController` for board-scoped label CRUD operations.
* Extend `TaskService` logic to support attaching and detaching labels via `task_labels` join table.
* Implement `CommentService` and `CommentController`. Enforce reverse-chronological data retrieval for task comments.

## Phase 8: Hardening & CI/CD
* Implement integration tests using `@SpringBootTest` and Testcontainers for PostgreSQL. Focus test coverage on authentication filters and concurrent task reordering.
* Implement multi-stage `Dockerfile` for Maven compilation and JRE execution.
* Configure `.github/workflows/build.yml` to trigger Maven build and test phases on repository push/pull request events.