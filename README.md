# Weather Map App (Spring Boot + Thymeleaf)

Production-style Java web application with:

- Bootstrap-based modern UI
- SVG district map with hover weather data
- Daily scheduler for JWT + weather sync
- DB-backed weather APIs for map and 5-day district history
- Thymeleaf server rendering

## Tech Stack

- Java 17
- Spring Boot 3
- Spring Web, Spring Data JPA, Thymeleaf
- H2 (default local) / PostgreSQL (production profile)

## API Flow

1. Scheduler runs daily (`weather.scheduler.cron`).
2. Calls auth API to fetch JWT token.
3. Uses JWT to call weather data API.
4. Upserts district weather snapshots into DB.
5. UI calls local APIs to render map data and district history.

## Local Run

```bash
mvn spring-boot:run
```

Open:

- UI: `http://localhost:8080`
- H2 Console: `http://localhost:8080/h2-console`

## Key Endpoints

- `GET /api/weather/map`  
  Returns latest weather snapshot per district (for map hover).

- `GET /api/weather/district/{districtCode}/history?days=5`  
  Returns district weather history up to 5 days.

## Config

Set via environment variables:

- `WEATHER_AUTH_URL`
- `WEATHER_DATA_URL`
- `WEATHER_CLIENT_ID`
- `WEATHER_CLIENT_SECRET`
- `WEATHER_SYNC_CRON`
- `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`

For prod:

```bash
SPRING_PROFILES_ACTIVE=prod
```
