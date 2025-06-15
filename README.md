# Booking System

A simple Spring Boot application for managing unit bookings with support for availability search, payments, and expiration handling.

## Technologies

- Java 21
- Spring Boot 3
- Spring Data JPA
- PostgreSQL
- Liquibase
- Gradle
- MapStruct
- Lombok

## Getting Started

### Prerequisites

- Java 21+
- PostgreSQL
- Gradle 8+

### Configuration

Update `application.yml` with your local PostgreSQL configuration:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/bookingsystem
    username: postgres
    password: postgres
  jpa:
    hibernate:
      ddl-auto: none
    show-sql: true
    properties:
      hibernate:
        format_sql: true
  liquibase:
    enabled: true
    drop-first: true
    change-log: classpath:db/changelog/db.changelog-master.yaml
payment:
  success:
    probability: 0.8
server:
  port: 8080
```

### Running the Application

```bash
./gradlew bootRun
```

### Building the Application

```bash
./gradlew build
```

### Running Tests

```bash
./gradlew test
```

## API Endpoints

- `POST /units` — Add new unit
- `GET /units/search` — Search for available units
- `GET /units/count` — Get number of available units
- `POST /bookings` — Book a unit
- `GET /payments` — Get all payments
- `POST /payments/process/{id}` — Manually process a payment (for testing)

## Features

- Add/search units with filters (number of rooms, type, floor, price range, availability)
- Book units for a selected date range
- Prevent booking conflicts
- Simulated asynchronous payments with expiration
- Periodic cleanup of expired payments
- In-memory caching for availability count
- RESTful API with DTO mapping via MapStruct

## Development Notes

- Uses virtual threads for payment emulation.
- 15-minute interval check for expired payments via `@Scheduled`.
- Booking/payment expiration modeled via `PaymentExpiration` entity.
- Markup price calculation is customizable via `application.yml` (optional).

## License

MIT