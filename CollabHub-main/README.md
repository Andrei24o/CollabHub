# CollabHub

Platformă internă pentru colaborare în echipă, gestionare task-uri și management de documente.

## Tehnologii utilizate

| Componentă       | Tehnologie                     |
|------------------|--------------------------------|
| Backend          | Java 17, Spring Boot 4.0      |
| Securitate       | Spring Security + JWT          |
| ORM              | Spring Data JPA / Hibernate    |
| Bază de date     | PostgreSQL 15                  |
| Migrații         | Flyway                         |
| Stocare fișiere  | MinIO (compatibil S3)          |
| Documentație API | Swagger / OpenAPI 3.0          |
| CI/CD            | GitHub Actions                 |
| Containerizare   | Docker + Docker Compose        |

## Cerințe preliminare

- **Java 17+**
- **Maven 3.9+**
- **Docker & Docker Compose**

## Pornirea aplicației

### Opțiunea 1: Rulare completă cu Docker Compose

Pornește PostgreSQL, MinIO și aplicația împreună:

```bash
cd collabhub
docker-compose up --build
```

API-ul va fi disponibil la `http://localhost:8080`.
Consola MinIO este la `http://localhost:9001` (utilizator: `minioadmin`, parolă: `minioadmin`).

### Opțiunea 2: Rulare locală (dezvoltare)

1. **Pornește infrastructura** (PostgreSQL + MinIO):

```bash
cd collabhub
docker-compose up postgres-db minio
```

2. **Creează bucket-ul MinIO** (doar prima dată):
   - Deschide `http://localhost:9001`, autentifică-te cu `minioadmin` / `minioadmin`
   - Creează un bucket cu numele `collabhub-docs`

3. **Rulează aplicația**:

```bash
cd collabhub
./mvnw spring-boot:run
```

## Endpoint-uri API

### Autentificare

| Metodă | Endpoint             | Descriere                        | Autentificare |
|--------|----------------------|----------------------------------|---------------|
| POST   | `/api/auth/register` | Înregistrare utilizator nou      | Nu            |
| POST   | `/api/auth/login`    | Autentificare și primire JWT     | Nu            |

### Gestionare utilizatori

| Metodă | Endpoint                      | Descriere                        | Autentificare |
|--------|-------------------------------|----------------------------------|---------------|
| GET    | `/api/users/me`               | Vizualizare profil propriu       | Da            |
| PUT    | `/api/users/me`               | Actualizare profil propriu       | Da            |

### Administrare (doar ROLE_ADMIN)

| Metodă | Endpoint                         | Descriere                        | Autentificare |
|--------|----------------------------------|----------------------------------|---------------|
| GET    | `/api/admin/users`               | Listare toți utilizatorii        | ADMIN         |
| PUT    | `/api/admin/users/{id}/role`     | Schimbare rol utilizator         | ADMIN         |
| PUT    | `/api/admin/users/{id}/status`   | Activare/dezactivare utilizator  | ADMIN         |

### Proiecte

| Metodă | Endpoint                        | Descriere                        | Autentificare |
|--------|---------------------------------|----------------------------------|---------------|
| GET    | `/api/projects`                 | Listare proiecte active          | Da            |
| POST   | `/api/projects`                 | Creare proiect nou               | Da            |
| PUT    | `/api/projects/{id}`            | Actualizare proiect              | Da            |
| DELETE | `/api/projects/{id}`            | Ștergere logică (soft delete)    | Da            |
| POST   | `/api/projects/{id}/members`    | Adăugare membru în proiect       | Da            |

### Task-uri

| Metodă | Endpoint                                              | Descriere                              | Autentificare |
|--------|-------------------------------------------------------|----------------------------------------|---------------|
| GET    | `/api/projects/{projectId}/tasks`                     | Listare task-uri (filtrare opțională)  | Da            |
| GET    | `/api/projects/{projectId}/tasks?status=X&priority=Y` | Filtrare task-uri după status/prioritate | Da          |
| POST   | `/api/projects/{projectId}/tasks`                     | Creare task nou                        | Da            |
| PUT    | `/api/projects/{projectId}/tasks/{taskId}`            | Actualizare task                       | Da            |
| DELETE | `/api/projects/{projectId}/tasks/{taskId}`            | Ștergere task                          | Da            |
| PUT    | `/api/projects/{projectId}/tasks/{taskId}/assign`     | Asignare utilizator la task            | Da            |

### Documente

| Metodă | Endpoint                                                       | Descriere                    | Autentificare |
|--------|----------------------------------------------------------------|------------------------------|---------------|
| GET    | `/api/projects/{projectId}/documents`                          | Listare documente proiect    | Da            |
| POST   | `/api/projects/{projectId}/documents`                          | Încărcare document           | Da            |
| GET    | `/api/projects/{projectId}/documents/download/{fileName}`      | Descărcare document          | Da            |
| DELETE | `/api/projects/{projectId}/documents/{documentId}`             | Ștergere document            | Da            |

## Exemple de cereri

### Înregistrare
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"john","email":"john@example.com","password":"secret123"}'
```

### Autentificare
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"john","password":"secret123"}'
```

### Creare proiect (folosește token-ul primit la autentificare)
```bash
curl -X POST http://localhost:8080/api/projects \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <TOKEN_JWT>" \
  -d '{"name":"Proiectul meu","description":"Un proiect exemplu","status":"ACTIVE"}'
```

### Creare task
```bash
curl -X POST http://localhost:8080/api/projects/1/tasks \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <TOKEN_JWT>" \
  -d '{"title":"Rezolvare bug","description":"Rezolvare bug la autentificare","priority":"HIGH","deadline":"2026-06-01T00:00:00"}'
```

### Încărcare document
```bash
curl -X POST http://localhost:8080/api/projects/1/documents \
  -H "Authorization: Bearer <TOKEN_JWT>" \
  -F "file=@/cale/catre/document.pdf"
```

## Structura proiectului

```
collabhub/
├── github/workflows/
├── src/main/java/com/collabhub/
│   ├── config/          # Configurare MinIO + OpenAPI
│   ├── controller/      # Controllere REST + tratare excepții
│   ├── model/           # Entități JPA și DTO-uri
│   ├── repository/      # Repository-uri Spring Data JPA
│   ├── security/        # JWT utils, filtru și configurare securitate
│   └── service/         # Logica de business + job-uri planificate
├── src/main/resources/
│   ├── database_scripts/ # Migrații Flyway
│   └── application.yml   # Configurare aplicație
├── .github/workflows/   # Pipeline CI/CD
├── Dockerfile
├── docker-compose.yml
└── pom.xml
```

## Bune practici backend

- **Arhitectură pe layere** — Separare clară între Controller (REST), Service (logică de business) și Repository (acces la date)
- **Injectare prin constructor** — Toate dependențele sunt injectate prin constructor folosind `@RequiredArgsConstructor` (fără injectare pe câmpuri)
- **Validare input** — Request body-urile sunt validate cu Bean Validation (`@Valid`, `@NotBlank`, `@Size`, `@Email`)
- **Tratare centralizată a excepțiilor** — `@RestControllerAdvice` cu `GlobalExceptionHandler` pentru răspunsuri de eroare consistente
- **Excepții custom** — `ResourceNotFoundException` pentru tratarea erorilor specifice domeniului
- **Logging structurat** — SLF4J (`@Slf4j`) utilizat în toate serviciile pentru trasabilitate
- **Autentificare stateless** — Autentificare bazată pe JWT fără sesiune pe server, hashing parole cu BCrypt
- **Control acces bazat pe roluri** — Securitate la nivel de metodă cu `@PreAuthorize` pentru endpoint-urile de admin
- **Ștergere logică (Soft Delete)** — Proiectele folosesc ștergere logică în loc de ștergere fizică
- **DTO-uri** — `UserProfileResponse` și `ProfileUpdateRequest` pentru decuplarea răspunsurilor API de entitățile JPA
- **Migrații bază de date** — Flyway pentru gestionarea versionată a schemei (fără `ddl-auto: update`)
- **Configurare externalizată** — Variabile de mediu cu valori implicite în `application.yml`
- **Infrastructură containerizată** — Docker Compose pentru PostgreSQL, MinIO și aplicație
- **Documentație API interactivă** — Swagger / OpenAPI 3.0 cu suport autentificare JWT prin `springdoc-openapi`
- **Job-uri automate în background** — Task `@Scheduled` care marchează automat task-urile expirate ca `OVERDUE`
- **Integrare continuă** — Pipeline GitHub Actions care compilează și testează proiectul la fiecare push

## Funcționalități noi

### 1. Documentație API interactivă — Swagger / OpenAPI 3.0

Aplicația generează automat o interfață web interactivă pentru testarea endpoint-urilor, fără a fi nevoie de Postman sau alte tool-uri externe. Configurația include suport pentru autentificare JWT direct din Swagger UI.

- **Dependență**: `springdoc-openapi-starter-webmvc-ui` în `pom.xml`
- **Configurare**: `OpenApiConfig.java` definește schema de securitate Bearer JWT
- **Acces**: `http://localhost:8080/swagger-ui.html`

### 2. Job automat pentru task-uri expirate — `@Scheduled`

Un serviciu care rulează automat în background și verifică periodic dacă există task-uri a căror deadline a trecut. Acestea sunt marcate automat cu statusul `OVERDUE`, fără intervenția utilizatorului.

- **Serviciu**: `TaskCleanupService.java` cu `@Scheduled(fixedRate = 10000)`
- **Activare**: `@EnableScheduling` pe clasa principală `CollabhubApplication`
- **Logare**: Fiecare execuție este logată cu numărul de task-uri marcate

### 3. Integrare continuă — GitHub Actions (CI)

La fiecare push pe branch-ul `main`, GitHub Actions pornește automat un pipeline care:
1. Instalează JDK 17
2. Compilează proiectul cu Maven
3. Rulează testele pentru a valida integritatea codului

- **Configurare**: `.github/workflows/build.yml`
- **Trigger**: Push și Pull Request pe branch-urile `main` / `master`

## Roluri

- `ROLE_USER` — Rol implicit la înregistrare. Poate gestiona propriul profil, crea/vizualiza proiecte, task-uri și documente.
- `ROLE_ADMIN` — Poate lista toți utilizatorii, schimba roluri și dezactiva conturi.

## Licență

Licență MIT
