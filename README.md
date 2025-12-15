# BookBox



## Aplikacja biblioteczna oparta na:

- Backend: Spring Boot (REST, JPA, Security)
- Frontend: React + Vite
- Baza danych: MySQL
- Orkiestracja: Docker Compose

## Struktura

- `backend/` – aplikacja Spring Boot
- `frontend/` – aplikacja React
- `docker-compose.yml` – uruchamianie całego stacku

Running the backend locally
---------------------------

You can run the backend with Docker Compose (it starts a MySQL service and the backend):

```powershell
docker compose up --build
```

Or run tests and start with embedded H2 (for development):

```powershell
cd backend
mvn test
mvn spring-boot:run
```

APIs available (basic):
- POST /api/users/register  { email, password, name }
- POST /api/users/login     { email, password }
- GET  /api/books
- GET  /api/books/search?query=...
- POST /api/loans  { userId, bookId }
- GET  /api/loans/user/{userId}

Useful scripts
-------------

We include a small PowerShell end-to-end smoke-test script that exercises the backend (registers a user, lists books, creates a loan, and verifies DB state):

```powershell
# from repo root
.\scripts\test-e2e.ps1
```

Run this after `docker compose up --build` to automatically verify the main backend flows.

Frontend
--------
We added a React + Vite frontend in `frontend/`. To run locally in development mode:

```powershell
cd frontend
npm install
npm run dev
```

Or run the production build via Docker Compose (served on http://localhost:3000):

```powershell
docker compose up --build
```

The frontend consumes the backend APIs under `/api/*`.

