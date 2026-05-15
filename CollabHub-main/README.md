# CollabHub

Internal company platform for team collaboration, task tracking, and document management.

## Tech Stack

| Component        | Technology                    |
|------------------|-------------------------------|
| Backend          | Java 17, Spring Boot 4.0      |
| Security         | Spring Security + JWT          |
| ORM              | Spring Data JPA / Hibernate    |
| Database         | PostgreSQL 15                  |
| Migrations       | Flyway                         |
| File Storage     | MinIO (S3-compatible)          |
| Containerization | Docker + Docker Compose        |

## Prerequisites

- **Java 17+**
- **Maven 3.9+**
- **Docker & Docker Compose**

## Getting Started

### Option 1: Run Everything with Docker Compose

This starts PostgreSQL, MinIO, and the application together:

```bash
cd collabhub
docker-compose up --build
```

The API will be available at `http://localhost:8080`.
MinIO console is at `http://localhost:9001` (user: `minioadmin`, password: `minioadmin`).

### Option 2: Run Locally (Development)

1. **Start infrastructure** (PostgreSQL + MinIO):

```bash
cd collabhub
docker-compose up postgres-db minio
```

2. **Create the MinIO bucket** (first time only):
   - Open `http://localhost:9001`, log in with `minioadmin` / `minioadmin`
   - Create a bucket named `collabhub-docs`

3. **Run the application**:

```bash
cd collabhub
./mvnw spring-boot:run
```

## API Endpoints

### Authentication

| Method | Endpoint             | Description              | Auth Required |
|--------|----------------------|--------------------------|---------------|
| POST   | `/api/auth/register` | Register a new user      | No            |
| POST   | `/api/auth/login`    | Login and receive JWT    | No            |

### User Management

| Method | Endpoint                      | Description                  | Auth Required |
|--------|-------------------------------|------------------------------|---------------|
| GET    | `/api/users/me`               | View own profile             | Yes           |
| PUT    | `/api/users/me`               | Update own profile           | Yes           |

### Admin (ROLE_ADMIN only)

| Method | Endpoint                         | Description               | Auth Required |
|--------|----------------------------------|---------------------------|---------------|
| GET    | `/api/admin/users`               | List all users            | ADMIN         |
| PUT    | `/api/admin/users/{id}/role`     | Change user role          | ADMIN         |
| PUT    | `/api/admin/users/{id}/status`   | Activate/deactivate user  | ADMIN         |

### Projects

| Method | Endpoint                        | Description              | Auth Required |
|--------|---------------------------------|--------------------------|---------------|
| GET    | `/api/projects`                 | List all active projects | Yes           |
| POST   | `/api/projects`                 | Create a new project     | Yes           |
| PUT    | `/api/projects/{id}`            | Update project info      | Yes           |
| DELETE | `/api/projects/{id}`            | Soft-delete a project    | Yes           |
| POST   | `/api/projects/{id}/members`    | Add member to project    | Yes           |

### Tasks

| Method | Endpoint                                              | Description                        | Auth Required |
|--------|-------------------------------------------------------|------------------------------------|---------------|
| GET    | `/api/projects/{projectId}/tasks`                     | List tasks (optional filter)       | Yes           |
| GET    | `/api/projects/{projectId}/tasks?status=X&priority=Y` | Filter tasks by status/priority    | Yes           |
| POST   | `/api/projects/{projectId}/tasks`                     | Create a new task                  | Yes           |
| PUT    | `/api/projects/{projectId}/tasks/{taskId}`            | Update a task                      | Yes           |
| DELETE | `/api/projects/{projectId}/tasks/{taskId}`            | Delete a task                      | Yes           |
| PUT    | `/api/projects/{projectId}/tasks/{taskId}/assign`     | Assign user to task                | Yes           |

### Documents

| Method | Endpoint                                                       | Description             | Auth Required |
|--------|----------------------------------------------------------------|-------------------------|---------------|
| GET    | `/api/projects/{projectId}/documents`                          | List project documents  | Yes           |
| POST   | `/api/projects/{projectId}/documents`                          | Upload a document       | Yes           |
| GET    | `/api/projects/{projectId}/documents/download/{fileName}`      | Download a document     | Yes           |
| DELETE | `/api/projects/{projectId}/documents/{documentId}`             | Delete a document       | Yes           |

## Example Requests

### Register
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"john","email":"john@example.com","password":"secret123"}'
```

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"john","password":"secret123"}'
```

### Create Project (use token from login response)
```bash
curl -X POST http://localhost:8080/api/projects \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <YOUR_JWT_TOKEN>" \
  -d '{"name":"My Project","description":"A sample project","status":"ACTIVE"}'
```

### Create Task
```bash
curl -X POST http://localhost:8080/api/projects/1/tasks \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <YOUR_JWT_TOKEN>" \
  -d '{"title":"Fix bug","description":"Fix login bug","priority":"HIGH","deadline":"2026-06-01T00:00:00"}'
```

### Upload Document
```bash
curl -X POST http://localhost:8080/api/projects/1/documents \
  -H "Authorization: Bearer <YOUR_JWT_TOKEN>" \
  -F "file=@/path/to/document.pdf"
```

## Project Structure

```
collabhub/
├── src/main/java/com/collabhub/
│   ├── config/          # MinIO configuration
│   ├── controller/      # REST controllers + exception handlers
│   ├── model/           # JPA entities and DTOs
│   ├── repository/      # Spring Data JPA repositories
│   ├── security/        # JWT utils, filter, and security config
│   └── service/         # Business logic layer
├── src/main/resources/
│   ├── database_scripts/ # Flyway migrations
│   └── application.yml   # Application configuration
├── Dockerfile
├── docker-compose.yml
└── pom.xml
```

## Backend Best Practices

- **Layered Architecture** — Clear separation between Controller (REST), Service (business logic), and Repository (data access) layers
- **Constructor Injection** — All dependencies injected via constructors using `@RequiredArgsConstructor` (no field injection)
- **Input Validation** — Request bodies validated with Bean Validation (`@Valid`, `@NotBlank`, `@Size`, `@Email`)
- **Centralized Exception Handling** — `@RestControllerAdvice` with `GlobalExceptionHandler` for consistent error responses
- **Custom Exceptions** — `ResourceNotFoundException` for domain-specific error handling
- **Structured Logging** — SLF4J (`@Slf4j`) used across all services for traceability
- **Stateless Authentication** — JWT-based auth with no server-side session, BCrypt password hashing
- **Role-Based Access Control** — Method-level security with `@PreAuthorize` for admin endpoints
- **Soft Delete** — Projects use logical deletion instead of physical removal
- **DTOs** — `UserProfileResponse` and `ProfileUpdateRequest` to decouple API responses from JPA entities
- **Database Migrations** — Flyway for versioned, repeatable schema management (no `ddl-auto: update`)
- **Externalized Configuration** — Environment variables with fallback defaults in `application.yml`
- **Containerized Infrastructure** — Docker Compose for PostgreSQL, MinIO, and the application

## Roles

- `ROLE_USER` — Default role for new registrations. Can manage own profile, create/view projects, tasks, and documents.
- `ROLE_ADMIN` — Can list all users, change roles, and deactivate accounts.

## License

MIT License
