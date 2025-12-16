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
Logowanie na konto admina:

- Email: admin@bookbox.local
- Password: admin

ewentualnie na stronie /admin w admin-key wpisujemy 'admin-secret'

## Pełna specyfikacja oraz omówienie w pliku MTAB.pdf