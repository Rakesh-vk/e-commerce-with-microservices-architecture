# E-Commerce Microservices Platform

A backend-focused **E-Commerce application built with Java 21, Spring Boot 4, Spring Data JPA, PostgreSQL, Spring Security, JWT, Kafka, Resilience4j, Spring Cloud Gateway, Eureka, REST clients, OpenAPI/Swagger, Docker Compose, and automated tests**.

The project is intentionally split into independently deployable services to demonstrate practical microservices fundamentals: service discovery, API gateway routing, event-driven communication, REST APIs, persistence, validation, authentication, inter-service communication, resilience, exception handling, DTO mapping, API documentation, and testing.

---

## Architecture

```text
                              ┌─────────────────────┐
                              │       Client        │
                              │  Swagger / Postman  │
                              └──────────┬──────────┘
                                         │
                                         ▼
                              ┌─────────────────────┐
                              │     ApiGateway       │
                              │        :8080         │
                              │  (dynamic routing)    │
                              └──────────┬──────────┘
                                         │
                   ┌─────────────────────┼─────────────────────────────┐
                   │                     │                             │
                   ▼                     ▼                             ▼
            ┌─────────────┐       ┌─────────────┐               ┌─────────────┐
            │ UserService │       │ProductService│               │OrderService │
            │    :8081    │       │    :8082    │               │    :8083    │
            └──────┬──────┘       └──────┬──────┘               └──────┬──────┘
                   │                     │                             │
                   ▼                     ▼                             │
            ┌─────────────┐       ┌─────────────┐                      │
            │  userdb     │       │  productdb  │◄─────────────────────┤
            └─────────────┘       └─────────────┘               REST / RestClient
                                                                        │
                                                                        ▼
                                                                 ┌─────────────┐
                                                                 │PaymentService│
                                                                 │    :8084    │
                                                                 └──────┬──────┘
                                                                        │
                                                                        ▼
                                                                 ┌─────────────┐
                                                                 │  paymentdb  │
                                                                 └─────────────┘

                         OrderService ──publishes──▶ Kafka topic: order.created
                              │                              │
                              ▼                              ▼
                         ┌───────────┐              ┌─────────────────────┐
                         │ orderdb   │              │  NotificationService │
                         └───────────┘              │        :8085         │
                                                     └──────────┬──────────┘
                                                                │
                                                     ┌──────────┴──────────┐
                                                     ▼                     ▼
                                              ┌───────────────┐    ┌───────────────┐
                                              │notification_db │    │  Email (SMTP) │
                                              └───────────────┘    └───────────────┘

              All services register with discovery-service (Eureka :8761)
```

### Services

| Service | Port | Responsibility | Database |
|---|---:|---|---|
| **discovery-service** | `8761` | Eureka service registry — all services register here | — |
| **ApiGateway** | `8080` | Single entry point; dynamically routes to services via Eureka discovery | — |
| **UserService** | `8081` | Registration, login, JWT authentication, secured endpoint | `userdb` |
| **ProductService** | `8082` | Product CRUD and stock management | `productdb` |
| **OrderService** | `8083` | Order creation/retrieval, product/stock/payment integration, publishes order events | `orderdb` |
| **PaymentService** | `8084` | Payment creation and retrieval | `paymentdb` |
| **NotificationService** | `8085` | Consumes order events from Kafka, sends email notifications, idempotent processing | `notification_db` |

Each service owns its persistence model rather than sharing JPA entities across services (database-per-service).

---

## Key Technical Highlights

### 1. Service Discovery & API Gateway

All services register with a Eureka server (`discovery-service`) on startup. `ApiGateway` uses Spring Cloud Gateway's dynamic discovery locator to route requests to the correct service by name, without hardcoding downstream URLs — adding a new service doesn't require touching gateway config.

### 2. Microservice Separation

The application is decomposed by business capability instead of building one large monolith:

- User management
- Product/catalog management
- Order management
- Payment management
- Notifications (event-driven)

Each service has its own Spring Boot application, Maven build, configuration, controllers, services, repositories, DTOs, entities, and exception handling.

### 3. Event-Driven Communication (Kafka)

`OrderService` publishes an `OrderCreatedEvent` to a Kafka topic (`order.created`) after an order is placed. `NotificationService` consumes it asynchronously and sends a confirmation email — decoupling order processing from notification delivery so a slow or failing mail provider never blocks checkout.

Design choices:

- **Choreography, not orchestration** — services react to events independently rather than a central coordinator directing each step.
- **Idempotent consumption** — `NotificationService` deduplicates on `(eventId, channel)` before processing, so Kafka's at-least-once delivery semantics (a message redelivered after a rebalance or restart) never result in a duplicate email.
- **One topic per event type**, plain JSON payloads — kept simple for a single-broker KRaft setup; a schema registry (e.g. Avro) is a natural next step if the event catalog grows.

### 4. Resilience (Resilience4j)

Inter-service REST calls from `OrderService` (to `ProductService` and `PaymentService`) are wrapped with Resilience4j circuit breakers, plus connect/read timeouts on the underlying `RestClient`. Each protected call has a dedicated fallback method, so a downstream outage degrades the request (e.g. a clear "service unavailable" error) instead of hanging or cascading a failure through the whole order flow.

### 5. REST API Design

The services expose REST endpoints using Spring Web MVC.

Examples:

```text
POST   /api/users/register
POST   /api/users/login
GET    /api/users/test

GET    /api/products
GET    /api/products/{id}
POST   /api/products
PATCH  /api/products
PATCH  /api/products/{id}/stock

POST   /api/orders
GET    /api/orders/{id}

POST   /api/payment
GET    /api/payment/{id}
```

HTTP status codes are used to represent successful operations and common error conditions such as `400`, `401`, `403`, `404`, `409`, and `500`.

---

## Authentication & Security

Every service validates JWTs independently (`JwtAuthenticationFilter` + `JwtTokenProvider`, shared `jwt.secret`) rather than relying on a single gateway checkpoint — a deliberate defense-in-depth choice: if one service's security config has a gap, the others aren't compromised alongside it. Centralizing JWT validation at `ApiGateway` as an additional early-rejection layer is a planned next step (see *Known Improvement Areas*).

- Spring Security
- JWT (via `jjwt`)
- BCrypt password encoding
- Role-based access control (`ROLE_USER`, `ROLE_ADMIN`)
- Stateless session management
- Custom `403`/`401` JSON responses (`CustomAccessDeniedHandler`, `CustomAuthenticationEntryPoint`)

The security configuration allows unauthenticated access to:

```text
/api/users/register
/api/users/login
/swagger-ui/**
/swagger-ui.html
/v3/api-docs/**
```

Other endpoints require authentication; some (product/order writes) additionally require `ROLE_ADMIN` or ownership checks.

JWT configuration is externalized through environment variables rather than hard-coded secrets.

---

## Inter-Service Communication

### Product integration

`OrderService` communicates with `ProductService` through Spring's `RestClient`, wrapped in a Resilience4j circuit breaker:

```text
OrderService
    │
    ├── GET   ProductService /api/products/{id}
    ├── PATCH ProductService /api/products/{id}/stock
    └── PATCH ProductService /api/products/{id}/stock/restore
```

The client maps remote HTTP errors into domain-specific exceptions such as `ProductNotFoundException` and `InsufficientStockException`, keeping HTTP details isolated inside the client layer.

### Payment integration

`OrderService` also contains a dedicated `PaymentServiceClient` (circuit-breaker protected) for calling:

```text
POST /api/payment
```

### Notification integration (asynchronous)

Rather than a REST call, `OrderService` publishes an event to Kafka and `NotificationService` consumes it independently:

```text
OrderService → Kafka topic "order.created" → NotificationService → Email
```

This is the one inter-service interaction that is fire-and-forget from `OrderService`'s perspective — it doesn't wait on or depend on notification delivery succeeding.

---

## Layered Architecture

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
PostgreSQL
```

With supporting layers:

```text
DTO
 ↕
Mapper

Client
 ↕
Remote Service (REST or Kafka)

Exception Handler
 ↕
Consistent API Errors
```

---

## DTO Mapping

The project avoids exposing JPA entities directly from controller APIs.

```text
Request DTO → Mapper → Entity → Repository → Entity → Mapper → Response DTO
```

Examples: `ProductCreateRequestDTO`, `ProductResponseDTO`, `CreateOrderRequestDTO`, `OrderResponseDTO`, `CreatePaymentRequestDTO`, `PaymentResponseDTO`, `UserRegisterRequest`, `UserResponse`.

---

## Validation & Error Handling

Request validation uses Jakarta Bean Validation (`@NotNull`, `@NotBlank`, `@Email`, `@Min`, `@Positive`) with `@Valid @RequestBody` on controllers.

Domain-specific exceptions replace low-level persistence/HTTP exceptions:

```text
UserService          ProductService              OrderService                  PaymentService        NotificationService
├── BadCredentialsException   ├── ProductNotFoundException   ├── OrderNotFoundException   ├── PaymentNotFoundException   └── NotificationException
└── DuplicateEmailException   └── InsufficientStockException ├── ProductNotFoundException └── PaymentFailedException
                                                              └── InsufficientStockException
```

Global exception handlers translate these into consistent API responses.

---

## Persistence

- Spring Data JPA, Hibernate, PostgreSQL, UUID identifiers

Each service uses a separate database — **database-per-service**:

```text
userdb  productdb  orderdb  paymentdb  notification_db
```

---

## API Documentation

OpenAPI/Swagger annotations (`@Operation`, `@ApiResponses`, `@Tag`) are used directly on controllers.

| Service | Swagger UI |
|---|---|
| UserService | `http://localhost:8081/swagger-ui/index.html` |
| ProductService | `http://localhost:8082/swagger-ui/index.html` |
| OrderService | `http://localhost:8083/swagger-ui/index.html` |
| PaymentService | `http://localhost:8084/swagger-ui/index.html` |
| NotificationService | `http://localhost:8085/swagger-ui/index.html` |

OpenAPI JSON: `http://localhost:<PORT>/v3/api-docs`

---

## Testing

### ProductService
Product creation, retrieval, update, not-found scenarios, stock updates, insufficient-stock scenarios, controller validation.

### OrderService
Order retrieval/creation, multiple order items, total calculation, product-service communication, product/stock error mapping, controller responses.

### PaymentService
Payment creation/retrieval, invalid requests, not-found scenarios, mapper behavior, controller validation.

### UserService
Registration, duplicate email handling, login validation, invalid credentials, DTO mapping, controller validation, secured endpoint behavior.

Run tests for an individual service from that service's directory:

```bash
mvn clean test
```

---

## Local Development

### Prerequisites

- Java 21
- Maven 3.9+
- PostgreSQL 16+ (local, not containerized in this setup)
- Docker (for Kafka)
- IntelliJ IDEA / another Java IDE
- Git

```bash
java -version   # expect major version 21
```

---

## Kafka Setup with Docker

```bash
docker-compose -f docker-compose-kafka.yml up -d
```

Starts a single-broker Kafka cluster (KRaft mode, no Zookeeper) on `localhost:9092`, with `order.created` auto-created on first publish.

## Database Setup

PostgreSQL runs locally (not containerized) in this setup. The application expects these databases to exist:

```text
userdb  productdb  orderdb  paymentdb  notification_db
```

Create them manually before starting the services if they don't already exist.

---

## Environment Configuration

Sensitive values are supplied through environment variables (loaded automatically via `springboot4-dotenv` from a `.env` file):

```text
DB_PASSWORD
POSTGRES_DB
POSTGRES_USER
JWT_SECRET
JWT_EXPIRATION_MS
```

**Do not commit real passwords, JWT secrets, API keys, or other credentials to Git.** Keep `.env` in `.gitignore`.

---

## Starting the Services

Recommended order — each depends on the ones before it being registered/available:

```text
1. PostgreSQL (local)
2. Kafka          → docker-compose -f docker-compose-kafka.yml up -d
3. discovery-service (Eureka)  → :8761
4. ApiGateway                  → :8080
5. UserService                 → :8081
6. ProductService               → :8082
7. PaymentService                → :8084
8. OrderService                   → :8083
9. NotificationService             → :8085
```

From each service's directory:

```bash
mvn spring-boot:run
```

(Or use an IDE run configuration — a compound/multi-run config with `discovery-service` set to launch first is convenient here.)

---

## Example API Flow

All requests go through the gateway at `http://localhost:8080`.

1. **Register** — `POST /api/users/register`
2. **Login** — `POST /api/users/login` → returns `{ "accessToken": "<JWT>" }`
3. **Create a product** — `POST /api/products` (admin)
4. **Create an order** — `POST /api/orders` — `OrderService` fetches product info and decreases stock via `ProductService`, processes payment via `PaymentService`, and publishes an `order.created` event
5. **Retrieve the order** — `GET /api/orders/{id}`
6. **Check notifications** — `NotificationService` consumes the event and sends a confirmation email (visible in the configured mail sink, e.g. Mailpit in local dev)

---

## Project Structure

```text
e_commerce/
│
├── discovery-service/
├── ApiGateway/
├── UserService/
│   └── src/main/java/com/ecommerce/UserService/
│       ├── config/  controller/  dto/  entity/
│       ├── exception/  mapper/  repository/
│       ├── security/  service/
├── ProductService/
├── OrderService/
│   └── src/main/java/com/ecommerce/OrderService/
│       ├── client/  (ProductServiceClient, PaymentServiceClient)
│       ├── producer/ (OrderEventProducer)
│       └── ...
├── PaymentService/
├── NotificationService/
│   └── src/main/java/com/ecommerce/NotificationService/
│       ├── consumer/  (NotificationKafkaConsumer)
│       ├── producer/  (NotificationProvider, EmailNotificationProvider)
│       ├── service/   entity/   repository/   event/
├── docker-compose.yml
├── docker-compose-kafka.yml
├── .gitignore
└── README.md
```

---

## Engineering Practices Demonstrated

- Java 21, Spring Boot 4
- Spring Web MVC, Spring Data JPA, Hibernate, PostgreSQL
- Spring Security, JWT authentication
- Spring Cloud Gateway + Eureka service discovery
- Kafka (event-driven, choreography saga, idempotent consumers)
- Resilience4j (circuit breakers, timeouts, fallbacks)
- REST API design, REST client integration
- Microservice boundaries, database-per-service architecture
- DTOs and mapping layers, Bean Validation, global exception handling
- OpenAPI/Swagger, Actuator, structured logging
- Docker Compose, Maven
- JUnit, Mockito, MockMvc, HTTP client testing
- Git / `.gitignore`

---

## Design Decisions

### Why separate services?
Each business capability can evolve and be deployed independently.

### Why choreography over orchestration for order events?
No central coordinator dictating each step — services react to events they care about. Simpler to reason about for a small number of event types; a saga orchestrator becomes more valuable as the workflow grows more branching steps.

### Why JWT validation duplicated per service instead of a shared library?
Each microservice is intended to eventually live in its own repository. A shared Java module for security code would create version lock-step coupling that undermines independent deployability. The validation logic itself is small and stable, and each service checking independently is a genuine defense-in-depth benefit — not just duplicated cost.

### Why DTOs?
Prevent persistence entities from becoming the public API contract.

### Why service clients?
Remote communication is isolated behind classes like `ProductServiceClient`/`PaymentServiceClient`/`OrderEventProducer`, keeping HTTP/Kafka details out of controllers and business logic.

### Why database-per-service?
Reduces coupling; each service owns its data model.

### Why Kafka for notifications specifically, but REST for product/payment?
Order confirmation emails don't need to block the checkout response, and a slow mail provider shouldn't fail an order. Product/payment calls, by contrast, are part of the order's own consistency (stock must actually be decremented, payment must actually succeed) — those stay synchronous with circuit-breaker protection instead.

---

## Known Improvement Areas

This repository is a learning/portfolio implementation rather than a production-ready commerce platform. Honest next steps:

- Centralize JWT validation at the gateway as an additional early-rejection layer (each service will still validate independently — see *Design Decisions*)
- Pagination on `GET /api/products`
- Docker Compose database initialization for all databases (currently only one auto-created via `POSTGRES_DB`; others created manually)
- Schema-first contracts for Kafka event payloads (e.g. Avro + schema registry) instead of shared Java DTOs, to prevent drift between services
- Correlation ID / distributed tracing across services
- Transaction/outbox strategy for cross-service workflows
- Database migrations with Flyway/Liquibase
- Integration tests using Testcontainers
- CI/CD pipeline
- Containerizing each Spring Boot service
- Production-grade observability (metrics, dashboards)
- Contract testing between services

---

## What This Project Shows a Recruiter

The value here isn't the number of CRUD endpoints — it's structuring a backend around **business capabilities and service boundaries**, then connecting those services through explicit synchronous (REST) and asynchronous (Kafka) contracts, with resilience and security handled deliberately rather than as an afterthought.

```text
Requirement → Domain Model → REST/Event API → Validation → Service Layer
    → Persistence → Inter-Service Communication (sync + async)
    → Resilience → Exception Handling → Security → Documentation → Tests
```

---

## Author

**Rakesh**

Java Backend Developer

```text
Java • Spring Boot • Microservices • Kafka • REST APIs
PostgreSQL • JPA/Hibernate • Spring Security • JWT
Resilience4j • Eureka • Spring Cloud Gateway
Testing • Docker • OpenAPI • Maven
```
