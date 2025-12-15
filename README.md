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

## Potrzebne narzedzia:
docker

## Uruchomienie
```powershell
docker compose up --build
```
served on http://localhost:3000

## Admin
When the backend is started it seeds a default admin user (for development):

- Email: admin@bookbox.local
- Password: admin

You can log in with this account to access the admin panel at `/admin` and manage books, users and loans.

For scripted API access you can pass header `X-ADMIN-KEY: admin-secret` (settable with `ADMIN_KEY` env var).

## Pełna specyfikacja w pliku MTAB.pdf