# Apfelkomplott Backend

Backend service for the Apfelkomplott game, implemented with Spring Boot. This project exposes REST endpoints to start a game, inspect the current game state, progress through phases, make investments, and access the production card market.

## Tech Stack

- Java 17
- Spring Boot 4
- Maven

## Requirements

- JDK 17 installed
- Maven, or use the included Maven Wrapper

## Run the Project

From the project root:

### Windows

```powershell
.\mvnw.cmd spring-boot:run
```

### macOS / Linux

```bash
./mvnw spring-boot:run
```

The backend runs on:

```text
http://localhost:8081
```

This project can also be opened and run in IntelliJ IDEA or VS Code as a standard Maven/Spring Boot project.

## Run Tests

### Windows

```powershell
.\mvnw.cmd test
```

### macOS / Linux

```bash
./mvnw test
```

## Main API Endpoints

Base path:

```text
/game
```

- `POST /game/start?mode=...`  
  Starts a new game with a selected farming mode.

- `POST /game/start-demo`  
  Starts a demo game state.

- `GET /game/state`  
  Returns the current game state.

- `POST /game/next-phase`  
  Advances the game to the next phase.

- `POST /game/invest`  
  Applies an investment action using a JSON request body.

- `POST /game/invest/production`  
  Buys a production card using a JSON request body.

- `GET /game/market`  
  Returns the currently visible market cards.

## Project Structure

- `src/main/java/com/apfelkomplott/apfelkomplott/controller`  
  REST controllers and DTOs

- `src/main/java/com/apfelkomplott/apfelkomplott/service`  
  Core game services and business logic

- `src/main/java/com/apfelkomplott/apfelkomplott/engine`  
  Round and phase execution logic

- `src/main/java/com/apfelkomplott/apfelkomplott/entity`  
  Game domain models

- `src/main/resources/static/cards`  
  Card images and card data

- `src/test/java`  
  Test sources

## Configuration

Important application settings are defined in `src/main/resources/application.properties`.

- Application name: `apfelkomplott`
- Server port: `8081`

## PostgreSQL Setup

The project now includes Spring Data JPA and PostgreSQL support, but it is kept behind a Spring profile so local development does not immediately depend on a running database.

To run with PostgreSQL enabled:

### Windows

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=postgres"
```

### macOS / Linux

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=postgres
```

Default PostgreSQL connection values are defined in `src/main/resources/application-postgres.properties`:

- Database: `apfelkomplott`
- Username: `postgres`
- Password: `postgres`
- URL: `jdbc:postgresql://localhost:5432/apfelkomplott`

You can override them with environment variables:

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`

This is the infrastructure step only. The current game state is still stored in memory and should be migrated to persisted entities next for real multiplayer support.

## Notes for Submission

- The backend currently allows cross-origin requests from `http://localhost:5173`, which suggests it is intended to work with a frontend running there.
- The project appears to keep game state in memory during runtime.
- If you submit this project to a professor, include this repository with the `README.md` so they can run it directly.

## Author

Add your name, student ID, course name, and submission date here before sharing.
