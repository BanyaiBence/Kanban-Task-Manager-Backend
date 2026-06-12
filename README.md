**Status: Work in progress**

# Kanban Task Manager (Spring Boot)

A Trello-style Kanban backend built with Java 21 and Spring Boot. The API allows authenticated users to manage boards, columns, tasks, labels, and comments with secure, multi-user access.

## Features (Planned)

- JWT-based authentication and stateless Spring Security
- CRUD operations for boards, columns, tasks, labels, and comments
- Lexicographical ordering for O(1) task and column reordering
- Soft deletes for boards and tasks
- Flyway-based SQL schema migrations and PostgreSQL persistence
- Global error handling with consistent JSON error responses

## Tech Stack

- **Backend:** Java 21, Spring Boot (Web, Data JPA, Validation, Security)
- **Database:** PostgreSQL + Flyway migrations
- **Build:** Maven
- **Testing:** JUnit, Spring Boot Test
- **DevOps:** Docker Compose for local Postgres, GitHub Actions CI (planned)

## Getting Started

1. Start PostgreSQL via Docker:

   ```bash
   docker-compose up -d
   ```

2. Configure local environment variables (see `.env.example`).

3. Run the application:

   ```bash
   ./mvnw spring-boot:run
   ```

4. The API will be available at `http://localhost:8080`.

## Project Structure

- `src/main/java/com/bencebanyai/kanban/kanbantaskmanager`
    - `config` – application-wide configuration (CORS, Jackson, Flyway, etc.)
    - `domain` – JPA entities and domain enums
    - `repository` – Spring Data JPA repositories
    - `service` – business logic and use cases
    - `security` – JWT auth and security configuration
    - `web`
        - `controller` – REST controllers
        - `dto` – request/response models
        - `exception` – global exception handling
        - `mapper` – mappings between entities and DTOs

## Documentation

- [Architecture Plan](docs/plans/Architecture%20Plan.md)
- [Backend Roadmap](docs/plans/Roadmap.md)