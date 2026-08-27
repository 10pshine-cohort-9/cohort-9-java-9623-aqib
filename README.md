# Contact Management System

A web-based contact management system built with Spring Boot (Java) and React.js.

Users can register (with email **or** phone), log in, and manage their personal contacts —
create, read, update, delete, search, and view detailed profiles (with labeled email
addresses and phone numbers).

## Tech Stack

| Layer        | Technology                                   |
|--------------|----------------------------------------------|
| Backend      | Java 17, Spring Boot 3, Spring Data JPA / Hibernate |
| Security     | Spring Security + JWT                         |
| Database     | PostgreSQL (H2 for tests)                     |
| Testing      | JUnit 5, Mockito, MockMvc, @DataJpaTest       |
| Logging      | SLF4J + Logback                               |
| Frontend     | React.js (Vite)                               |
| Quality      | SonarQube, CodeRabbit                         |
| Version Ctrl | Git (feature branches + PRs)                 |

## Project Structure

```
contact-management-system/
├── backend/      # Spring Boot REST API
└── frontend/     # React.js SPA
```

## Prerequisites

- Java 17+
- Maven 3.8+
- Node.js 20+ and npm
- PostgreSQL 14+

## Running the Backend

```bash
cd backend
# Option A: Edit backend/.env with your PostgreSQL credentials (auto-loaded)
# Option B: Set environment variables before running:
export DB_URL="jdbc:postgresql://localhost:5432/contacts?sslmode=disable"
export DB_USERNAME="postgres"
export DB_PASSWORD="postgres123"

mvn spring-boot:run
# API available at http://localhost:8080
```

The `.env` file in `backend/` contains default dev credentials:
```
DB_URL=jdbc:postgresql://localhost:5432/contacts?sslmode=disable
DB_USERNAME=postgres
DB_PASSWORD=postgres123
JWT_SECRET=<base64-encoded-secret>
```
To use it, either export the vars (`source backend/.env` won't work directly since
it uses `KEY=VALUE` format — use `set -a && source backend/.env && set +a`) or set
them in your IDE/shell environment.

## Running the Frontend

```bash
cd frontend
npm install
npm run dev
# UI available at http://localhost:3000
```

## Running Tests

```bash
cd backend
mvn test         # runs all unit + slice tests (H2)
```

Tests cover three layers:
- **Controllers** — `MockMvc` slice tests (`controller/` package)
- **Services** — Mockito unit tests (`service/` package)
- **Repositories** — `@DataJpaTest` slice tests with H2 (`repository/` package)

## SonarQube Analysis

```bash
cd backend
mvn clean verify -Psonar \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=<your-token>
```

## API Endpoints

### Auth
| Method | Path                | Description            |
|--------|---------------------|------------------------|
| POST   | `/api/auth/register`| Register (email/phone) |
| POST   | `/api/auth/login`   | Login, returns JWT      |
| POST   | `/api/auth/refresh`  | Refresh access token   |

### Users
| Method | Path                  | Description         |
|--------|-----------------------|---------------------|
| GET    | `/api/users/me`       | Current user profile|
| PUT    | `/api/users/me/password` | Change password   |
| POST   | `/api/users/logout`   | Logout             |

### Contacts
| Method | Path              | Description                    |
|--------|-------------------|--------------------------------|
| GET    | `/api/contacts`   | Paginated list (search, page, size) |
| POST   | `/api/contacts`   | Create contact                 |
| GET    | `/api/contacts/{id}` | View contact detail         |
| PUT    | `/api/contacts/{id}` | Update contact              |
| DELETE | `/api/contacts/{id}` | Delete contact              |

## Code Quality

- `coderabbit.yaml` enforces: exception handling (HIGH), no raw types/unchecked casts (MEDIUM), OOP principles.
- Global exception handling via `@RestControllerAdvice` returns consistent `ErrorResponse` payloads.
- All layers use generics (no raw types) and SLF4J logging.
