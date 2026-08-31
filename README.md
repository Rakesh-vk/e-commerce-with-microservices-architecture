# E-Commerce Microservices Platform

A backend-focused **E-Commerce application built with Java 21, Spring Boot 4, Spring Data JPA, PostgreSQL, Spring Security, JWT, REST clients, OpenAPI/Swagger, Docker Compose, and automated tests**.

The project is intentionally split into independently deployable services to demonstrate practical microservices fundamentals: service boundaries, REST APIs, persistence, validation, authentication, inter-service communication, exception handling, DTO mapping, API documentation, observability, and testing.

---

## Architecture

```text
                         ┌─────────────────────┐
                         │       Client        │
                         │  Swagger / Postman  │
                         └──────────┬──────────┘
                                    │
              ┌─────────────────────┼─────────────────────┐
              │                     │                     │
              ▼                     ▼                     ▼
       ┌─────────────┐       ┌─────────────┐       ┌─────────────┐
       │ UserService │       │ProductService│       │OrderService │
       │    :8081    │       │    :8082    │       │    :8083    │
       └──────┬──────┘       └──────┬──────┘       └──────┬──────┘
              │                     │                     │
              ▼                     ▼                     │
       ┌─────────────┐       ┌─────────────┐              │
       │  userdb     │       │  productdb  │◄─────────────┤
       └─────────────┘       └─────────────┘              │
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
                                                   ┌─────────────┐
                                                   │  paymentdb  │
                                                   └─────────────┘

                         OrderService
                              │
                              ▼
                         ┌───────────┐
                         │ orderdb   │
                         └───────────┘
```

### Services

| Service | Port | Responsibility | Database |
|---|---:|---|---|
| **UserService** | `8081` | Registration, login, JWT authentication, secured endpoint | `userdb` |
| **ProductService** | `8082` | Product CRUD and stock management | `productdb` |
| **OrderService** | `8083` | Order creation, order retrieval, product/stock integration | `orderdb` |
| **PaymentService** | `8084` | Payment creation and payment retrieval | `paymentdb` |

Each service owns its persistence model rather than sharing JPA entities across services.

---

## Key Technical Highlights

### 1. Microservice Separation

The application is decomposed by business capability instead of building one large monolith:

- User management
- Product/catalog management
- Order management
- Payment management

Each service has its own Spring Boot application, Maven build, configuration, controllers, services, repositories, DTOs, entities, and exception handling.

### 2. REST API Design

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

HTTP status codes are used to represent successful operations and common error conditions such as `400`, `401`, `404`, `409`, and `500`.

---

## Authentication & Security

`UserService` implements stateless authentication using:

- Spring Security
- JWT
- BCrypt password encoding
- JWT authentication filter
- Stateless session management
- Secured endpoints
- Public authentication endpoints

The security configuration allows unauthenticated access to:

```text
/api/users/register
/api/users/login
/swagger-ui/**
/swagger-ui.html
/v3/api-docs/**
```

Other endpoints require authentication.

JWT configuration is externalized through environment variables rather than hard-coding secrets into Java source code.

---

## Inter-Service Communication

`OrderService` communicates with other services through Spring's `RestClient`.

### Product integration

Order creation:

```text
OrderService
    │
    ├── GET ProductService /api/products/{id}
    │
    └── PATCH ProductService /api/products/{id}/stock
```

The client maps remote HTTP errors into domain-specific exceptions such as:

- `ProductNotFoundException`
- `InsufficientStockException`

This keeps HTTP communication details isolated inside the client layer.

### Payment integration

`OrderService` also contains a dedicated `PaymentServiceClient` for calling:

```text
POST /api/payment
```

The client maps the request/response into dedicated DTOs rather than sharing payment-service entities.

---

## Layered Architecture

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

Exception Handler
 ↕
Consistent API Errors
```

This keeps responsibilities separated and makes individual components easier to test and maintain.

---

## DTO Mapping

The project avoids exposing JPA entities directly from controller APIs.

Instead:

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

This demonstrates separation between persistence models and API contracts.

---

## Validation & Error Handling

Request validation is implemented using Jakarta Bean Validation.

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

Domain-specific exceptions are used instead of leaking low-level persistence or HTTP exceptions through the application.

Examples:

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
```

Global exception handlers translate these exceptions into API responses.

---

## Persistence

Persistence is implemented with:

- Spring Data JPA
- Hibernate
- PostgreSQL
- UUID identifiers
- Entity relationships
- Repository abstractions

Each service uses a separate database:

```text
userdb
productdb
orderdb
paymentdb
```

This reflects the **database-per-service** approach and avoids tightly coupling services through shared persistence.

---

## API Documentation

OpenAPI/Swagger annotations are used directly on the controllers.

Examples include:

```java
@Operation
@ApiResponses
@ApiResponse
@Tag
```

### Swagger UI

| Service | Swagger UI |
|---|---|
| UserService | `http://localhost:8081/swagger-ui/index.html` |
| ProductService | `http://localhost:8082/swagger-ui/index.html` |
| OrderService | `http://localhost:8083/swagger-ui/index.html` |
| PaymentService | `http://localhost:8084/swagger-ui/index.html` |

OpenAPI JSON is available at:

```text
http://localhost:<PORT>/v3/api-docs
```

For `UserService`, JWT bearer authentication is also represented in the OpenAPI configuration.

---

## Testing

The project includes unit and controller/API-focused tests.

Testing covers areas such as:

### ProductService

- Product creation
- Product retrieval
- Product update
- Product-not-found scenarios
- Stock updates
- Insufficient-stock scenarios
- Controller validation and HTTP responses

### OrderService

- Order retrieval
- Order-not-found handling
- Order creation
- Multiple order items
- Total amount calculation
- Product-service communication
- Product/stock error mapping
- Controller responses

### PaymentService

- Payment creation
- Payment retrieval
- Invalid payment requests
- Payment-not-found scenarios
- Mapper behavior
- Controller validation

### UserService

- User registration
- Duplicate email handling
- Login validation
- Invalid credentials
- DTO mapping
- Controller validation
- Secured endpoint behavior

Run all tests for an individual service from that service directory:

```bash
mvn clean test
```

Or:

```bash
./mvnw clean test
```

---

## Local Development

### Prerequisites

Install:

- Java 21
- Maven 3.9+
- PostgreSQL 16+ OR Docker
- IntelliJ IDEA / another Java IDE
- Git

Verify Java:

```bash
java -version
```

Expected major version:

```text
21
```

---

## Database Setup with Docker

The repository includes:

```text
docker-compose.yml
```

It starts PostgreSQL 16:

```bash
docker compose up -d
```

Check the container:

```bash
docker ps
```

Stop it:

```bash
docker compose down
```

Persistent PostgreSQL data is stored in the Docker volume:

```text
postgres_data
```

The application expects these databases:

```text
userdb
productdb
orderdb
paymentdb
```

If they are not created automatically in your local PostgreSQL setup, create them before starting the services.

---

## Environment Configuration

Sensitive values should be supplied through environment variables.

Required variables include:

```text
DB_PASSWORD
POSTGRES_DB
POSTGRES_USER
JWT_SECRET
JWT_EXPIRATION_MS
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

The repository currently contains a `.env` file in the project archive. Before publishing this project publicly, remove any real credentials from it and keep `.env` ignored by Git.

---

## Starting the Services

Start PostgreSQL:

```bash
docker compose up -d
```

Then start each service from its own directory.

### UserService

```bash
cd UserService
mvn spring-boot:run
```

Runs on:

```text
http://localhost:8081
```

### ProductService

```bash
cd ProductService
mvn spring-boot:run
```

Runs on:

```text
http://localhost:8082
```

### OrderService

```bash
cd OrderService
mvn spring-boot:run
```

Runs on:

```text
http://localhost:8083
```

### PaymentService

```bash
cd PaymentService
mvn spring-boot:run
```

Runs on:

```text
http://localhost:8084
```

---

## Recommended Startup Order

For local development:

```text
1. PostgreSQL
       ↓
2. UserService
       ↓
3. ProductService
       ↓
4. PaymentService
       ↓
5. OrderService
```

`OrderService` depends on the Product and Payment service endpoints being configured.

The current configuration uses:

```text
product-service.base-url=http://localhost:8082
payment-service.base-url=http://localhost:8084
```

---

## Example API Flow

A typical development flow is:

### 1. Register

```http
POST /api/users/register
```

Create a user account.

### 2. Login

```http
POST /api/users/login
```

Receive:

```json
{
  "accessToken": "<JWT>"
}
```

### 3. Create a product

```http
POST /api/products
```

### 4. Create an order

```http
POST /api/orders
```

`OrderService` retrieves product information and decreases stock through `ProductService`.

### 5. Retrieve the order

```http
GET /api/orders/{id}
```

### 6. Process/retrieve payment

```http
POST /api/payment
GET /api/payment/{id}
```

---

## Project Structure

```text
e_commerce/
│
├── UserService/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/ecommerce/UserService/
│   │   │   │   ├── config/
│   │   │   │   ├── controller/
│   │   │   │   ├── dto/
│   │   │   │   ├── entity/
│   │   │   │   ├── exception/
│   │   │   │   ├── mapper/
│   │   │   │   ├── repository/
│   │   │   │   ├── security/
│   │   │   │   └── service/
│   │   │   └── resources/
│   │   └── test/
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
├── docker-compose.yml
├── .gitignore
└── README.md
```

---

## Engineering Practices Demonstrated

This project demonstrates practical experience with:

- **Java 21**
- **Spring Boot 4**
- Spring Web MVC
- Spring Data JPA
- Hibernate
- PostgreSQL
- Spring Security
- JWT authentication
- REST API design
- REST client integration
- Microservice boundaries
- Database-per-service architecture
- DTOs and mapping layers
- Bean Validation
- Global exception handling
- OpenAPI / Swagger
- Actuator
- Structured logging
- Docker Compose
- Maven
- JUnit
- Mockito
- MockMvc
- HTTP client testing
- Git / `.gitignore`

---

## Design Decisions

### Why separate services?

The application separates business capabilities so that each service can evolve and be deployed independently.

### Why DTOs?

DTOs prevent persistence entities from becoming the public API contract and provide a clean boundary between the database and REST layer.

### Why service clients?

Remote communication is isolated behind classes such as:

```text
ProductServiceClient
PaymentServiceClient
```

This prevents HTTP communication code from leaking into controllers and keeps business logic easier to reason about.

### Why database-per-service?

It reduces coupling between services and allows each service to own its data model.

### Why JWT?

JWT provides stateless authentication suitable for REST APIs and avoids storing server-side HTTP session state for authenticated requests.

---

## Known Improvement Areas

This repository is a learning/project implementation rather than a production-ready commerce platform. The next engineering improvements would include:

- API Gateway
- Service discovery
- Centralized configuration
- Distributed tracing
- Resilience4j retries/circuit breakers
- Message broker integration such as Kafka/RabbitMQ
- Transaction/outbox strategy for cross-service workflows
- Idempotency for payment/order operations
- Centralized authentication/authorization
- Database migrations with Flyway/Liquibase
- Integration tests using Testcontainers
- CI/CD pipeline
- Containerizing each Spring Boot service
- Production-grade observability
- More comprehensive security tests
- Contract testing between services

These are intentionally identified as future work rather than claiming functionality that is not currently implemented.

---

## What This Project Shows a Recruiter

The main value of this project is not the number of CRUD endpoints. It demonstrates the ability to structure a backend around **business capabilities and service boundaries**, then connect those services through explicit API contracts.

The implementation covers the core backend engineering workflow:

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
Exception Handling
    ↓
Security
    ↓
Documentation
    ↓
Automated Tests
```

That makes the repository useful as a demonstration of **backend and microservices engineering fundamentals**, rather than just a collection of Spring Boot CRUD examples.

---

## Author

**Rakesh**

Backend / Software Developer

Focus areas demonstrated in this project:

```text
Java • Spring Boot • REST APIs • Microservices
PostgreSQL • JPA/Hibernate • Spring Security • JWT
Testing • Docker • OpenAPI • Maven
```
