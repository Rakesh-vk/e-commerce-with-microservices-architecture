# E-Commerce Microservices Platform

A backend-focused **E-Commerce microservices application** built with Java, Spring Boot, Spring Data JPA, PostgreSQL, Spring Security, JWT, REST clients, Apache Kafka, OpenAPI/Swagger, Eureka service discovery, Spring Cloud Gateway, Mailpit, Docker Compose, and automated tests.

The project is structured as independently deployable services to demonstrate practical backend and microservices concepts including service boundaries, REST APIs, persistence, authentication, inter-service communication, asynchronous event-driven communication, notification delivery, validation, exception handling, DTO mapping, API documentation, observability, and testing.

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
                              │     ApiGateway      │
                              │        :8080        │
                              └──────────┬──────────┘
                                         │
                     ┌───────────────────┼───────────────────┐
                     │                   │                   │
                     ▼                   ▼                   ▼
              ┌─────────────┐     ┌─────────────┐     ┌─────────────┐
              │ UserService  │     │ProductService│     │ OrderService│
              │    :8081     │     │    :8082     │     │    :8083    │
              └──────┬──────┘     └──────┬──────┘     └──────┬──────┘
                     │                   │                   │
                     ▼                   ▼                   │
                ┌─────────┐         ┌───────────┐            │
                │ userdb  │         │ productdb │◄───────────┤
                └─────────┘         └───────────┘            │
                                                             │
                                                   REST / RestClient
                                                             │
                                                             ▼
                                                      ┌─────────────┐
                                                      │PaymentService│
                                                      │    :8084    │
                                                      └──────┬──────┘
                                                             │
                                                             ▼
                                                        ┌─────────┐
                                                        │paymentdb │
                                                        └─────────┘

                         OrderService
                              │
                              │ OrderCreatedEvent
                              ▼
                       ┌────────────────┐
                       │ Apache Kafka    │
                       │ order.created   │
                       └───────┬────────┘
                               │
                               ▼
                       ┌──────────────────┐
                       │NotificationService│
                       │      :8085       │
                       └────────┬─────────┘
                                │
                     ┌──────────┴──────────┐
                     ▼                     ▼
              notification_db          Mailpit
                                      SMTP :1025
                                      UI :8025


                    ┌──────────────────────┐
                    │   Discovery Service  │
                    │        :8761         │
                    │       Eureka         │
                    └──────────────────────┘
```

---

## Services

| Service | Port | Responsibility | Database |
|---|---:|---|---|
| **ApiGateway** | `8080` | API entry point and dynamic service discovery/routing | — |
| **UserService** | `8081` | Registration, login, JWT authentication, user APIs | `userdb` |
| **ProductService** | `8082` | Product CRUD and stock management | `productdb` |
| **OrderService** | `8083` | Order creation/retrieval and product/payment integration | `orderdb` |
| **PaymentService** | `8084` | Payment creation and payment retrieval | `paymentdb` |
| **NotificationService** | `8085` | Consumes order events and creates/delivers notifications | `notification_db` |
| **Discovery Service** | `8761` | Eureka service registry/discovery | — |

Each service owns its persistence model rather than sharing JPA entities across services.

---

# Technology Stack

- Java
- Spring Boot
- Spring Web MVC
- Spring Data JPA
- Hibernate
- PostgreSQL
- Spring Security
- JWT / JJWT
- Spring Cloud Netflix Eureka
- Spring Cloud Gateway
- Spring `RestClient`
- Apache Kafka
- Spring Kafka
- Spring Mail
- Mailpit
- OpenAPI / Swagger UI
- Spring Boot Actuator
- Maven
- JUnit
- Mockito
- MockMvc
- Docker Compose

---

# Architecture Patterns Demonstrated

The project demonstrates several important microservices patterns:

- Database-per-service
- Synchronous REST communication
- Asynchronous event-driven communication
- Service discovery
- API Gateway
- JWT-based stateless authentication
- DTO-based API contracts
- Layered architecture
- Global exception handling
- Event consumers and producers
- Notification provider abstraction
- Local SMTP email testing
- Health and actuator endpoints
- Automated testing

---

# API Gateway

`ApiGateway` runs on:

```text
http://localhost:8080
```

It uses Spring Cloud Gateway with Eureka service discovery.

Dynamic discovery is enabled through:

```properties
spring.cloud.gateway.server.webflux.discovery.locator.enabled=true
spring.cloud.gateway.server.webflux.discovery.locator.lower-case-service-id=true
```

The gateway can discover registered services through Eureka instead of requiring every service route to be hard-coded.

---

# Service Discovery

The Eureka Discovery Service runs on:

```text
http://localhost:8761
```

Services register themselves with Eureka using:

```text
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
```

The current architecture uses Eureka for service registration/discovery.

---

# REST API Design

The services expose REST endpoints using Spring Web MVC.

## UserService

```text
POST   /api/users/register
POST   /api/users/login
GET    /api/users/test
GET    /api/users
```

## ProductService

```text
GET    /api/products
GET    /api/products/{id}
POST   /api/products
PATCH  /api/products
PATCH  /api/products/{id}/stock
PATCH  /api/products/{id}/stock/restore
```

## OrderService

```text
POST   /api/orders
GET    /api/orders/{id}
```

## PaymentService

```text
POST   /api/payment
GET    /api/payment/{id}
```

## NotificationService

Notification APIs are exposed through `NotificationController`. The service also receives asynchronous order events through Kafka.

HTTP status codes are used to represent successful operations and common error conditions such as `400`, `401`, `404`, `409`, and `500`.

---

# Authentication & Security

`UserService` implements stateless authentication using:

- Spring Security
- JWT
- BCrypt password encoding
- JWT authentication filter
- Stateless session management
- Secured endpoints
- Public authentication endpoints

Authentication endpoints include:

```text
/api/users/register
/api/users/login
```

Swagger/OpenAPI endpoints are also configured for public access where required.

JWT configuration is externalized through environment variables rather than hard-coded secrets.

JWT claims currently include information such as:

```text
sub  -> user ID
role -> user role
email -> user email
```

Other services validate the JWT using their configured JWT provider/filter.

---

# Inter-Service Communication

The project uses **two communication styles**.

## 1. Synchronous REST Communication

`OrderService` communicates with other services through Spring's `RestClient`.

### Product integration

During order processing:

```text
OrderService
    │
    ├── GET ProductService /api/products/{id}
    │
    └── PATCH ProductService /api/products/{id}/stock
```

The client layer maps remote HTTP errors into domain-specific exceptions such as:

- `ProductNotFoundException`
- `InsufficientStockException`

This keeps HTTP communication details isolated inside the client layer.

### Payment integration

`OrderService` also contains a dedicated `PaymentServiceClient` for calling:

```text
POST /api/payment
```

The client uses dedicated request/response DTOs rather than sharing PaymentService entities.

---

# Apache Kafka

Apache Kafka is used for **asynchronous communication** between OrderService and NotificationService.

Kafka is configured separately using:

```text
docker-compose-kafka.yml
```

The Kafka broker is exposed on:

```text
localhost:9092
```

## Event Flow

When an order is created:

```text
OrderService
     │
     │ publish OrderCreatedEvent
     ▼
Kafka
     │
     │ topic: order.created
     ▼
NotificationService
     │
     ├── Save notification
     │
     └── Deliver email
```

The producer publishes to:

```text
order.created
```

using the order ID as the Kafka message key.

The NotificationService consumes the same topic using:

```text
notification-service
```

as its consumer group.

---

# OrderCreatedEvent

The event currently contains:

```text
eventId
orderId
userId
customerEmail
orderAmount
status
createdAt
```

Example structure:

```java
public record OrderCreatedEvent(
        UUID eventId,
        UUID orderId,
        UUID userId,
        String customerEmail,
        BigDecimal orderAmount,
        OrderStatus status,
        LocalDateTime createdAt
) {}
```

The NotificationService maintains its own local event representation rather than depending on OrderService Java classes.

Kafka JSON type headers are disabled on the consumer so the consumer does not depend on the producer's Java fully-qualified class name.

The NotificationService is configured with:

```properties
spring.kafka.consumer.properties[spring.json.value.default.type]=com.ecommerce.NotificationService.event.OrderCreatedEvent
spring.kafka.consumer.properties[spring.json.use.type.headers]=false
spring.kafka.consumer.properties[spring.json.trusted.packages]=com.ecommerce.NotificationService.event
```

This keeps the consumer's Java model local to NotificationService.

---

# NotificationService

`NotificationService` runs on:

```text
http://localhost:8085
```

Its responsibilities include:

1. Consume `OrderCreatedEvent` from Kafka.
2. Create a notification record.
3. Persist notification state.
4. Dispatch the notification through a provider.
5. Send email through SMTP.
6. Record delivery failures.

The email implementation is abstracted behind:

```text
NotificationProvider
        │
        ▼
EmailNotificationProvider
```

This makes it possible to add other notification channels/providers later.

---

# Mailpit

For local development, the project uses **Mailpit** instead of Gmail SMTP.

Mailpit is a local SMTP server and web-based email testing inbox.

It prevents development emails from being sent to real users.

## Mailpit Ports

```text
SMTP: localhost:1025
Web UI: http://localhost:8025
```

Start Mailpit:

```bash
mailpit
```

Stop Mailpit:

```text
Ctrl + C
```

Run Mailpit in the background:

```bash
mailpit > /tmp/mailpit.log 2>&1 &
```

Stop the background process:

```bash
pkill mailpit
```

Open the Mailpit inbox:

```text
http://localhost:8025
```

## Spring Mail Configuration

NotificationService uses:

```properties
spring.mail.host=localhost
spring.mail.port=1025
spring.mail.username=
spring.mail.password=

spring.mail.properties.mail.smtp.auth=false
spring.mail.properties.mail.smtp.starttls.enable=false
```

The email flow is therefore:

```text
OrderService
      ↓
Kafka
      ↓
NotificationService
      ↓
EmailNotificationProvider
      ↓
Mailpit SMTP :1025
      ↓
Mailpit Web UI :8025
```

For production, Mailpit should be replaced with a real transactional email provider.

---

# Layered Architecture

The services follow a conventional layered design:

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
Remote Service

Producer / Consumer
 ↕
Kafka

Provider
 ↕
External Delivery System

Exception Handler
 ↕
Consistent API Errors
```

This keeps responsibilities separated and makes individual components easier to test and maintain.

---

# DTO Mapping

The project avoids exposing JPA entities directly from controller APIs.

The general flow is:

```text
Request DTO
    ↓
Mapper
    ↓
Entity
    ↓
Repository
    ↓
Entity
    ↓
Mapper
    ↓
Response DTO
```

Examples include:

- `ProductCreateRequestDTO`
- `ProductUpdateRequestDTO`
- `ProductResponseDTO`
- `CreateOrderRequestDTO`
- `OrderResponseDTO`
- `CreatePaymentRequestDTO`
- `PaymentResponseDTO`
- `UserRegisterRequest`
- `UserResponse`
- `NotificationResponseDTO`

DTOs provide an explicit API boundary between persistence models and external clients.

---

# Validation & Error Handling

Request validation uses Jakarta Bean Validation.

Examples include:

```java
@NotNull
@NotBlank
@Email
@Min
@Positive
```

Controllers use:

```java
@Valid
@RequestBody
```

Domain-specific exceptions prevent low-level persistence or HTTP implementation details from leaking through the application.

Examples include:

```text
UserService
├── BadCredentialsException
└── DuplicateEmailException

ProductService
├── ProductNotFoundException
└── InsufficientStockException

OrderService
├── OrderNotFoundException
├── ProductNotFoundException
└── InsufficientStockException

PaymentService
├── PaymentNotFoundException
└── PaymentFailedException

NotificationService
└── NotificationDeliveryException
```

Global exception handlers translate exceptions into API responses.

---

# Persistence

Persistence is implemented with:

- Spring Data JPA
- Hibernate
- PostgreSQL
- UUID identifiers
- Entity relationships
- Repository abstractions

Each business service owns its own database:

```text
userdb
productdb
orderdb
paymentdb
notification_db
```

This follows the **database-per-service** approach and avoids tightly coupling services through shared persistence.

---

# API Documentation

OpenAPI/Swagger annotations are used directly on controllers.

Examples include:

```java
@Operation
@ApiResponses
@ApiResponse
@Tag
```

## Swagger UI

| Service | Swagger UI |
|---|---|
| UserService | `http://localhost:8081/swagger-ui/index.html` |
| ProductService | `http://localhost:8082/swagger-ui/index.html` |
| OrderService | `http://localhost:8083/swagger-ui/index.html` |
| PaymentService | `http://localhost:8084/swagger-ui/index.html` |
| NotificationService | `http://localhost:8085/swagger-ui/index.html` |

OpenAPI JSON is available at:

```text
http://localhost:<PORT>/v3/api-docs
```

The UserService OpenAPI configuration includes JWT bearer authentication.

---

# Actuator & Observability

Spring Boot Actuator is included in the services where configured.

Actuator endpoints can be used for:

- Health checks
- Application information
- Mappings
- Configuration inspection
- Gateway information

The current project also uses application logging for debugging and operational visibility.

**Distributed tracing is a future improvement** and is not currently represented as a completed feature in this repository.

---

# Testing

The project includes unit and controller/API-focused tests.

Testing covers areas such as:

## ProductService

- Product creation
- Product retrieval
- Product update
- Product-not-found scenarios
- Stock updates
- Stock restoration
- Insufficient-stock scenarios
- Controller validation and HTTP responses

## OrderService

- Order retrieval
- Order-not-found handling
- Order creation
- Multiple order items
- Total amount calculation
- Product-service communication
- Product/stock error mapping
- Controller responses

## PaymentService

- Payment creation
- Payment retrieval
- Invalid payment requests
- Payment-not-found scenarios
- Mapper behavior
- Controller validation

## UserService

- User registration
- Duplicate email handling
- Login validation
- Invalid credentials
- DTO mapping
- Controller validation
- Secured endpoint behavior

## NotificationService

- Spring context/application tests
- Notification event consumption components
- Notification persistence and delivery components

Run all tests for an individual service from that service directory:

```bash
mvn clean test
```

Or:

```bash
./mvnw clean test
```

---

# Local Development

## Prerequisites

Install:

- Java
- Maven 3.9+
- PostgreSQL 16+ OR Docker
- Docker Desktop if running PostgreSQL/Kafka through Docker
- Mailpit
- IntelliJ IDEA or another Java IDE
- Git

Verify Java:

```bash
java -version
```

The project source currently uses Java/Spring Boot versions defined in each service's `pom.xml`. Check the individual Maven configuration if your local Java version differs.

---

# Environment Configuration

Sensitive values should be supplied through environment variables.

The repository `.env` currently defines:

```text
DB_PASSWORD
POSTGRES_DB
POSTGRES_USER
JWT_EXPIRATION_MS
JWT_SECRET
```

Example:

```text
DB_PASSWORD=your-password
POSTGRES_DB=userdb
POSTGRES_USER=root
JWT_SECRET=your-long-random-secret
JWT_EXPIRATION_MS=86400000
```

**Do not commit real passwords, JWT secrets, API keys, or other credentials to Git.**

Before publishing the repository publicly:

1. Remove real credentials from `.env`.
2. Keep `.env` ignored by Git.
3. Provide a `.env.example` containing placeholder values.

---

# Database Setup with Docker

The repository includes:

```text
docker-compose.yml
```

Start PostgreSQL:

```bash
docker compose up -d
```

Check the container:

```bash
docker ps
```

Stop PostgreSQL:

```bash
docker compose down
```

Persistent PostgreSQL data is stored in:

```text
postgres_data
```

The application expects these databases:

```text
userdb
productdb
orderdb
paymentdb
notification_db
```

If the databases are not created automatically in your local PostgreSQL setup, create them before starting the services.

---

# Kafka Setup with Docker

Kafka has a separate Compose file:

```text
docker-compose-kafka.yml
```

Start Kafka:

```bash
docker compose -f docker-compose-kafka.yml up -d
```

Check Kafka:

```bash
docker ps
```

Stop Kafka:

```bash
docker compose -f docker-compose-kafka.yml down
```

Kafka is exposed on:

```text
localhost:9092
```

The configured topic used by the current order notification flow is:

```text
order.created
```

Kafka data is stored in the Docker volume:

```text
kafka_data
```

---

# Starting the Services

For local development, a practical startup sequence is:

```text
1. PostgreSQL
       ↓
2. Kafka
       ↓
3. Discovery Service
       ↓
4. UserService
       ↓
5. ProductService
       ↓
6. PaymentService
       ↓
7. NotificationService
       ↓
8. OrderService
       ↓
9. ApiGateway
```

Mailpit can be started alongside the services before testing notification delivery.

---

## Discovery Service

```bash
cd discovery-service
mvn spring-boot:run
```

Runs on:

```text
http://localhost:8761
```

---

## UserService

```bash
cd UserService
mvn spring-boot:run
```

Runs on:

```text
http://localhost:8081
```

---

## ProductService

```bash
cd ProductService
mvn spring-boot:run
```

Runs on:

```text
http://localhost:8082
```

---

## PaymentService

```bash
cd PaymentService
mvn spring-boot:run
```

Runs on:

```text
http://localhost:8084
```

---

## NotificationService

```bash
cd NotificationService
mvn spring-boot:run
```

Runs on:

```text
http://localhost:8085
```

---

## OrderService

```bash
cd OrderService
mvn spring-boot:run
```

Runs on:

```text
http://localhost:8083
```

---

## ApiGateway

```bash
cd ApiGateway
mvn spring-boot:run
```

Runs on:

```text
http://localhost:8080
```

---

# Example End-to-End Flow

A typical development flow is:

### 1. Start infrastructure

```bash
docker compose up -d
docker compose -f docker-compose-kafka.yml up -d
mailpit
```

### 2. Start Discovery Service

```text
http://localhost:8761
```

### 3. Start application services

Start:

```text
UserService
ProductService
PaymentService
NotificationService
OrderService
ApiGateway
```

### 4. Register a user

```http
POST /api/users/register
```

### 5. Login

```http
POST /api/users/login
```

Receive a JWT access token.

### 6. Create a product

```http
POST /api/products
```

### 7. Create an order

```http
POST /api/orders
```

During order creation, OrderService communicates with ProductService and PaymentService as required by the order workflow.

### 8. Publish order event

After order processing, OrderService publishes:

```text
OrderCreatedEvent
```

to:

```text
order.created
```

### 9. NotificationService consumes the event

NotificationService:

```text
Kafka
  ↓
OrderCreatedEvent
  ↓
Create notification
  ↓
Save notification
  ↓
EmailNotificationProvider
```

### 10. View the email

Open:

```text
http://localhost:8025
```

The email should appear in the Mailpit inbox.

---

# Project Structure

```text
e_commerce/
│
├── ApiGateway/
│   ├── src/
│   └── pom.xml
│
├── UserService/
│   ├── src/
│   └── pom.xml
│
├── ProductService/
│   ├── src/
│   └── pom.xml
│
├── OrderService/
│   ├── src/
│   └── pom.xml
│
├── PaymentService/
│   ├── src/
│   └── pom.xml
│
├── NotificationService/
│   ├── src/
│   └── pom.xml
│
├── discovery-service/
│   ├── src/
│   └── pom.xml
│
├── docker-compose.yml
├── docker-compose-kafka.yml
├── .env
├── .gitignore
└── README.md
```

---

# Key Components

## UserService

```text
controller
dto
entity
exception
mapper
repository
security
service
```

Responsible for authentication, users, JWT generation and user APIs.

## ProductService

```text
controller
dto
entity
exception
mapper
repository
service
```

Responsible for product/catalog and stock operations.

## OrderService

```text
controller
client
dto
entity
event
exception
mapper
producer
repository
security
service
```

Responsible for orders, remote product/payment communication and publishing order events.

## PaymentService

```text
controller
dto
entity
exception
mapper
repository
service
```

Responsible for payment operations.

## NotificationService

```text
consumer
controller
dto
entity
event
exception
producer
repository
security
service
```

Responsible for consuming order events, persisting notifications and delivering email notifications.

---

# Engineering Practices Demonstrated

This project demonstrates practical experience with:

- Java
- Spring Boot
- Spring Web MVC
- Spring Data JPA
- Hibernate
- PostgreSQL
- Spring Security
- JWT authentication
- REST API design
- Spring `RestClient`
- Microservice boundaries
- Eureka service discovery
- Spring Cloud Gateway
- Database-per-service architecture
- Apache Kafka
- Event producers and consumers
- DTOs and mapping layers
- Bean Validation
- Global exception handling
- OpenAPI / Swagger
- Actuator
- Structured logging
- SMTP email integration
- Mailpit local email testing
- Docker Compose
- Maven
- JUnit
- Mockito
- MockMvc
- Git / `.gitignore`

---

# Design Decisions

## Why separate services?

The application separates business capabilities so that each service can evolve and be deployed independently.

## Why DTOs?

DTOs prevent persistence entities from becoming the public API contract and provide a clean boundary between the database and REST layer.

## Why service clients?

Remote communication is isolated behind classes such as:

```text
ProductServiceClient
PaymentServiceClient
```

This prevents HTTP communication code from leaking into controllers and keeps business logic easier to reason about.

## Why database-per-service?

Each business service owns its own data. This reduces coupling and allows services to evolve their persistence models independently.

## Why JWT?

JWT provides stateless authentication suitable for REST APIs and avoids storing server-side HTTP session state for authenticated requests.

## Why Kafka?

Kafka provides asynchronous communication between services and decouples notification processing from the synchronous order request.

The current example is:

```text
OrderService
      ↓
Kafka
      ↓
NotificationService
```

This allows NotificationService to process the order event independently.

## Why Mailpit?

Mailpit provides a safe local SMTP environment for development. It allows the application to execute the complete email-delivery path without sending real emails.

---

# Current Limitations / Future Improvements

This repository is a learning/project implementation rather than a production-ready commerce platform.

Potential next engineering improvements include:

- Distributed tracing
- Centralized configuration
- Resilience4j retries and circuit breakers
- Transactional Outbox pattern
- Kafka consumer idempotency
- Dead-letter topics / DLT handling
- Schema Registry and Avro/Protobuf/JSON Schema
- API Gateway authentication/authorization
- Centralized authentication/authorization
- Database migrations with Flyway/Liquibase
- Integration tests using Testcontainers
- CI/CD pipeline
- Containerizing each Spring Boot service
- Production-grade Kafka configuration
- Production-grade observability
- Metrics dashboards
- Centralized log aggregation
- Rate limiting
- API versioning
- Distributed tracing with Micrometer Tracing/OpenTelemetry
- More comprehensive security tests
- Contract testing between services

These are listed as future work rather than claiming functionality that is not currently implemented.

---

# What This Project Demonstrates

The main value of this project is not the number of CRUD endpoints. It demonstrates how to structure a backend around **business capabilities and service boundaries**, and how to connect those services through both synchronous and asynchronous communication.

The current architecture demonstrates:

```text
                    Client
                      ↓
                 API Gateway
                      ↓
               Service Discovery
                      ↓
        ┌─────────────┴─────────────┐
        │                           │
     REST APIs                   Kafka Events
        │                           │
        ▼                           ▼
  Product / Payment           Notification
        │                           │
        ▼                           ▼
    PostgreSQL                   PostgreSQL
                                    │
                                    ▼
                                  Mailpit
```

The overall backend engineering workflow is:

```text
Requirement
    ↓
Domain Model
    ↓
REST API
    ↓
Validation
    ↓
Service Layer
    ↓
Persistence
    ↓
Inter-Service Communication
    ↓
Asynchronous Events
    ↓
Notification
    ↓
Exception Handling
    ↓
Security
    ↓
Documentation
    ↓
Testing
    ↓
Observability
```

---

# Author

**Rakesh**

Backend / Software Developer

Focus areas demonstrated in this project:

```text
Java • Spring Boot • REST APIs • Microservices
PostgreSQL • JPA/Hibernate • Spring Security • JWT
Kafka • Eureka • API Gateway • RestClient
Testing • Docker • OpenAPI • Maven • Mailpit
```
