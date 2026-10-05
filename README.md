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

- `GET /game/state`  
  Returns the current game state.

- `GET /game/help`  
  Returns a structured "how to play" guide for onboarding popups or help screens.

- `GET /game/help/current-phase`  
  Returns player-friendly help text for the current game phase.

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

### JSON request validation

JSON request bodies are validated using Jackson and Jakarta Bean Validation.
Invalid bodies return HTTP `400` with a JSON `message` field before game actions run.
This applies to both the legacy `/game/...` and `/game/{gameId}/...` action endpoints.

- Production purchases require a nonblank `cardId` string.
- Event selections require a nonnegative integer `optionIndex`; fractional values are rejected.
- Investments require an `investmentType` name: `BUY_SEEDLING`, `BUY_PRE_GROWN_TREE`,
  `BUY_CRATE`, or `BUY_SALES_STAND`. Numeric enum values are rejected.
- Unknown properties, malformed JSON, and extra content after the JSON body are rejected.

These checks use the existing validation dependency; no JSON Schema library is required.
Jackson settings below apply to the application's shared mapper, including card data loading.

Important application settings are defined in `src/main/resources/application.properties`.

- Application name: `apfelkomplott`
- Server port: `8081`


## Deployment (Hochschule Fulda VM)

The backend is deployed on an Ubuntu 26.04 LTS virtual machine provided by Hochschule Fulda.

### Production Environment

- Ubuntu 26.04 LTS
- Java 17
- Nginx reverse proxy
- Spring Boot backend running as a systemd service
- Frontend served separately by Nginx

### Build

```bash
./mvnw clean package


```md
## Deployment Notes

The backend listens on port 8081 by default. In production, external access is provided through Nginx, which proxies requests to the Spring Boot application.

Card images are served from the `/cards` endpoint via Spring Boot static resources.
