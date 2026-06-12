# Spring Boot Kanban Task Manager – Architecture Plan

## Overview
This document describes the architectural overview for the backend of a Kanban task manager application.
The application allows authenticated users to manage boards, columns, tasks, labels, and comments in a Trello‑style Kanban interface, with emphasis on sound database modeling, and layered architecture.

## High‑Level Requirements

- Users can register, log in, and manage their own boards.
- Each board contains ordered columns (e.g. Todo, In Progress, Done).
- Each column contains ordered tasks with title, description, due date, assignee, labels, and comments.
- Users can drag and drop tasks between columns and reorder tasks within a column.
- Users can filter tasks by label, assignee, and partial text.
- All operations are exposed via a REST API secured for the current user, and consumed by the frontend.

## Technology Stack

### Backend

- **Language & Framework:** Java 21, Spring Boot (Web, Validation, Spring Data JPA).
- **Database:** PostgreSQL, accessed via Hibernate/JPA.
- **Schema Migrations:** Flyway for versioned SQL migrations.
- **Build Tool:** Maven for dependency management and builds.
- **Security:** Spring Security with stateless JWT authentication for protecting API endpoints.
- **Testing:** JUnit for unit and integration tests, Spring Boot testing support.

### DevOps & Tooling

- **Containerization:** Dockerfile for the backend and docker‑compose including PostgreSQL for easy setup.
- **CI:** GitHub Actions workflow to run Maven build and tests on push/PR.

## Architectural Style

The backend follows a strict **Traditional N-Tier (Layered) Architecture**. This approach provides a clear, universally understood separation of concerns, keeping the codebase easily navigable and maintainable without overcomplicating the dependency graph.

- **Presentation Layer (Web):** REST controllers that handle HTTP requests/responses, validate inputs, and map internal models to Data Transfer Objects (DTOs).
- **Application/Service Layer:** Services containing transactional business logic, orchestrating calls between repositories, and handling optimistic locking exceptions.
- **Domain Layer:** JPA entities, value objects, and domain enums representing boards, columns, tasks, users, labels, and comments.
- **Infrastructure/Data Layer:** Spring Data JPA repositories, database configuration, and third-party integrations.

This separation ensures that database entities are never exposed directly to the API clients and that business logic is strictly decoupled from HTTP concerns.

## Backend Package Structure

A conventional structure under `com.yourname.kanban`:

- `config`
  - Application‑wide configuration (CORS, Jackson, Flyway properties, etc.).
- `domain`
  - JPA entities, enums, and value objects.
- `repository`
  - Spring Data JPA repositories for each aggregate.
- `service`
  - Business services implementing use cases (e.g. board management, task movement).
- `web`
  - `controller` – REST controllers.
  - `dto` – request/response models.
  - `mapper` – mapping logic between entities and DTOs (manual or MapStruct).
  - `exception` – global exception handler and standardized error payload.
- `security` 
  - Security configuration, JWT filter, user details service, and auth controllers.

This layout aligns with common Spring Boot best practices and keeps concerns separated and discoverable.

## Domain Model and Data Design

### Core Entities

1. **User**
   - Fields: `id`, `email`, `passwordHash`, `displayName`, `createdAt`.
   - Relations: one‑to‑many with `Board` as owner; optional assignee for `Task` and author of `Comment`.

2. **Board**
   - Fields: `id`, `name`, `description`, `createdAt`, `updatedAt`, `isArchived`.
   - Relations: many‑to‑one `owner` (`User`); one‑to‑many `Column`.

3. **Column**
   - Fields: `id`, `name`, `position` (**String**, using lexicographical ordering).
   - Relations: many‑to‑one `board`; one‑to‑many `Task`.

4. **Task**
   - Fields: `id`, `title`, `description`, `position` (**String**, using lexicographical ordering), `dueDate`, `createdAt`, `updatedAt`, `isArchived`.
   - Relations: many‑to‑one `column`; optional many‑to‑one `assignee` (`User`); many‑to‑many `Label`; one‑to‑many `Comment`.

5. **Label**
   - Fields: `id`, `name`, `color`.
   - Relations: many‑to‑many with `Task` via join table `task_labels`.

6. **Comment**
   - Fields: `id`, `body`, `createdAt`.
   - Relations: many‑to‑one `task`; many‑to‑one `author` (`User`).

### Concurrency & Locking

To safely handle collaborative environments where multiple users or sessions might update the same board simultaneously, the system uses **Optimistic Locking**. Key entities (like `Task` and `Board`) include a `@Version Long version` field. Spring Data JPA automatically checks this version during updates, throwing an `ObjectOptimisticLockingFailureException` if a concurrent modification is detected, preventing silent data overwrites.

### Database Considerations

- Use normalized tables with explicit foreign keys (e.g. `board_id`, `column_id`, `task_id`, `user_id`).
- Add indexes on frequent query paths, such as `(board_id, position)` for columns and `(column_id, position)` for tasks.
- Use `is_archived` instead of hard deletes to keep history while simplifying queries.
- Start with a single owner per board; if time allows, extend with a `board_members` join table for collaboration.

## Repository Layer

Spring Data JPA repositories provide CRUD operations and derived queries.

- `UserRepository` – `findByEmail`, existence checks for registration and login.
- `BoardRepository` – `findAllByOwnerId` with pagination, `findByIdAndOwnerId` for access control.
- `ColumnRepository` – `findByBoardIdOrderByPosition`.
- `TaskRepository` – `findByColumnBoardIdAndFilters` (using derived queries or `@Query` with filters for label, assignee, and search text), returning a `Page` for pagination.
- `LabelRepository` – basic CRUD and lookup by board if labels are scoped per board.
- `CommentRepository` – `findByTaskIdOrderByCreatedAt`.

## Service Layer and Use Cases

Service classes encapsulate business operations and transactions.

### BoardService

- Create, rename, archive, and delete boards (soft delete).
- Ensure only the owner can access/modify specific boards.
- When deleting a board, mark associated columns and tasks as archived if necessary.

### ColumnService

- Create new columns with sequential `position` values.
- Rename columns and update their `position` to support column reordering.

### TaskService

- Create tasks in a column with a `position` at the end of that column.
- Update tasks (title, description, due date, assignee, labels, archive flag).
- Move tasks between columns and reorder tasks within a column, recalculating `position` within a single transaction using `@Transactional` to avoid inconsistent states.
- Implement search and filter logic by delegating to repository methods and returning paged results.

### LabelService

- Create, update, and delete labels scoped to a board.
- Attach/detach labels from tasks.

### CommentService

- Add comments to tasks and list comments for a task in reverse chronological order.

## Web Layer (REST API Design)

REST controllers expose operations through versioned endpoints (e.g. `/api/v1/...`). Controllers accept and return DTOs, not entities, to keep internal details decoupled from the API contract.

### Authentication

- `POST /api/auth/register` – register a new user.
- `POST /api/auth/login` – authenticate and return a JWT token.

Subsequent requests include the JWT in the `Authorization` header and are validated by Spring Security.

### Board Endpoints

- `GET /api/boards` – list boards for current user (paginated).
- `POST /api/boards` – create a new board.
- `GET /api/boards/{boardId}` – get board details, optionally with columns summary.
- `PATCH /api/boards/{boardId}` – rename or archive a board.
- `DELETE /api/boards/{boardId}` – soft delete a board.

### Column Endpoints

- `POST /api/boards/{boardId}/columns` – create a column in a board.
- `PATCH /api/columns/{columnId}` – update column name/position.
- `DELETE /api/columns/{columnId}` – archive column and its tasks, or reassign tasks.

### Task Endpoints

- `GET /api/boards/{boardId}/tasks` – list tasks for a board with optional filters: `columnId`, `labelId`, `assigneeId`, `search`, `page`, `size`.
- `POST /api/columns/{columnId}/tasks` – create a new task in a column.
- `GET /api/tasks/{taskId}` – task details, including labels and comments summary.
- `PATCH /api/tasks/{taskId}` – update task properties (including moving column and `position`).
- `DELETE /api/tasks/{taskId}` – soft delete a task.

### Label Endpoints

- `GET /api/boards/{boardId}/labels` – list labels for a board.
- `POST /api/boards/{boardId}/labels` – create label.
- `PATCH /api/labels/{labelId}` – update label.
- `DELETE /api/labels/{labelId}` – delete label.

### Comment Endpoints

- `GET /api/tasks/{taskId}/comments` – list comments for a task.
- `POST /api/tasks/{taskId}/comments` – add a new comment.

## Validation and Error Handling

- Use Bean Validation annotations on DTOs (e.g. `@NotBlank`, `@Size`, `@Email`, `@PastOrPresent` for timestamps) to validate incoming data.
- Implement a `@ControllerAdvice` with an exception handler for validation errors, entity not found, and access denied, returning consistent error responses (HTTP status, machine‑readable error code, message).
- Ensure security checks (e.g. board ownership) throw dedicated exceptions so they are mapped to appropriate HTTP statuses.

## Frontend Architecture

The React frontend is a moderate SPA focusing on usability and clear interaction with the REST API.

## Non‑Functional Requirements

- **Performance:** Use pagination for task lists, avoid returning entire board graphs on every request; consider light DTOs for lists and heavier DTOs for detail views.
- **Security & Granular Authorization:** All non-auth endpoints are protected via stateless JWT validation. Furthermore, domain-level authorization is handled using Spring Security's method-level security (`@PreAuthorize`). Custom security beans (e.g., `@PreAuthorize("@boardSecurity.isOwner(#boardId)")`) are used at the controller level to verify resource ownership, keeping the service layer purely focused on business logic rather than access control checks.
- **Maintainability:** Keep controllers thin and push logic into services; avoid exposing entities directly.
- **Testability:** Write unit tests for services, and integration tests for key flows (e.g. moving tasks, creating boards).

## Implementation Roadmap (High Level)

1. **Setup & Infrastructure**
   - Initialize Spring Boot project with necessary dependencies and configure PostgreSQL + Flyway.

2. **Core Domain & CRUD**
   - Implement entities, repositories, and straightforward CRUD services/controllers for users, boards, columns, and tasks.

3. **Kanban Features**
   - Implement task ordering and movement logic with transactional updates.
   - Add labels and comments, and implement search/filter endpoints.

4. **Polish & Portfolio Enhancements**
   - Add tests, Docker setup, GitHub Actions CI.
   
5. **Frontend Integration**

---

## Useful References

1. [Hexagonal Architecture Template with Java and Spring Boot](https://kamilmazurek.pl/hexagonal-architecture-template)
2. [Hexagonal vs Clean Architecture in Spring Boot - Trinity Logic](https://www.trinitylogic.co.uk/blog/hexagonal-vs-clean-architecture/)
3. [Hexagonal Architecture in Spring Boot: A Practical Guide](https://dev.to/jhonifaber/hexagonal-architecture-or-port-adapters-23ed)

