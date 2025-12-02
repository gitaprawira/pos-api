# Soloware POS API

A production-ready Point of Sale (POS) system backend built with Spring Boot 4.0.0, featuring JWT authentication, role-based access control, and comprehensive product management.

## 📖 Table of Contents

- [Quick Start](#-quick-start)
- [Features](#-features)
- [Architecture](#️-architecture)
- [API Endpoints](#-api-endpoints)
- [Security Configuration](#-security-configuration)
- [Database Schema](#️-database-schema)
- [Error Handling](#️-error-handling)
- [Development](#️-development)
- [Best Practices](#-best-practices-implemented)

## 🚀 Quick Start

### Prerequisites

- Java 25
- PostgreSQL 12+
- Maven 3.9+

### Setup & Run

1.**Install Java 25 JDK** (if not already installed):

```bash
sudo apt install openjdk-25-jdk-headless
```

2.**Create PostgreSQL database**:

```sql
CREATE DATABASE pos_db;
CREATE USER pos_user WITH PASSWORD 'mysecretpassword';
GRANT ALL PRIVILEGES ON DATABASE pos_db TO pos_user;
```

3.**Update `application.yml`** if needed:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/pos_db
    username: pos_user
    password: mysecretpassword
```

4.**Run the application**:

```bash
./start.sh
```

Or manually:

```bash
export JAVA_HOME=/usr/lib/jvm/java-25-openjdk-amd64
./mvnw spring-boot:run
```

The API will be available at: `http://localhost:8080`

### Access API Documentation

Once the application is running, visit:

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

### Default Admin Credentials

- **Username**: `admin`
- **Password**: `admin123`
- **Email**: `admin@soloware.id`
- **Role**: `ADMIN`

## 📋 Features

✅ **JWT-based authentication** (stateless sessions)  
✅ **Role-based access control** (USER, ADMIN, MANAGER)  
✅ **Product management** with CRUD operations  
✅ **SKU-based product lookup**  
✅ **Stock quantity management**  
✅ **Price management** with validation  
✅ **Comprehensive validation** (Jakarta Bean Validation)  
✅ **Global exception handling**  
✅ **Database migrations** with Flyway  
✅ **API documentation** with Swagger/OpenAPI 3  
✅ **Method-level security** with `@PreAuthorize`  
✅ **Password encryption** using BCrypt  
✅ **Structured logging** with Logstash for ELK stack  
✅ **Application metrics** exposed via Prometheus  
✅ **Request tracing** with unique request IDs (MDC)  
✅ **Health checks** via Spring Boot Actuator  

## 🏗️ Architecture

### Tech Stack

- **Framework**: Spring Boot 4.0.0 (Java 25)
- **Security**: Spring Security with JWT authentication
- **Database**: PostgreSQL with Flyway migrations
- **ORM**: Spring Data JPA
- **API Documentation**: Swagger/OpenAPI 3
- **Build Tool**: Maven
- **Code Quality**: Lombok for boilerplate reduction
- **Logging**: SLF4J/Logback with Logstash encoder
- **Monitoring**: Micrometer, Prometheus, Spring Boot Actuator
- **Observability**: Structured JSON logging with MDC context

### Project Structure

The project follows a **vertical slice architecture** pattern:

```text
com.soloware.pos/
├── config/             # Configuration classes
│   ├── SecurityConfig.java
│   ├── SwaggerConfig.java
│   ├── WebConfig.java
│   └── CorsConfig.java
├── core/               # Core utilities and shared components
│   ├── annotation/     # Custom annotations (@CurrentUser, @AuthCheck)
│   ├── enums/          # Global enums (Role)
│   ├── exception/      # Global exception handler
│   ├── interceptor/    # JWT authentication filter
│   ├── resolver/       # Argument resolvers
│   └── utils/          # Utility classes (JwtUtil, ApiResponse)
└── modules/            # Feature modules
    ├── auth/           # Authentication module
    │   ├── controller/
    │   ├── dto/
    │   ├── entity/
    │   ├── repository/
    │   └── service/
    └── product/        # Product management module
        ├── controller/
        ├── dto/
        ├── entity/
        ├── repository/
        └── service/
```

### Security Highlights

- **Passwords encrypted** with BCrypt
- **Stateless JWT authentication** (no server-side sessions)
- **Method-level security** with `@PreAuthorize`
- **CSRF protection** (disabled for API)
- **Role-based endpoint protection**
- **Token expiration**: 24 hours (86400000 ms)

## 🔌 API Endpoints

### Authentication Endpoints

#### Register a New User

```http
POST /api/v1/auth/register
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "password123",
  "role": "USER"
}
```

**Response:**

```json
{
  "statusCode": 200,
  "message": "User registered successfully",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "username": "john_doe",
    "email": "john@example.com",
    "role": "USER"
  }
}
```

#### Login

```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

**Response:**

```json
{
  "statusCode": 200,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "username": "admin",
    "email": "admin@soloware.id",
    "role": "ADMIN"
  }
}
```

#### Get Current User

```http
GET /api/v1/auth/me
Authorization: Bearer <token>
```

### Product Endpoints

#### Create Product (Admin/Manager only)

```http
POST /api/v1/products
Authorization: Bearer <token>
Content-Type: application/json

{
  "sku": "PROD-001",
  "name": "Sample Product",
  "price": 99.99,
  "stockQuantity": 100
}
```

#### Get All Products

```http
GET /api/v1/products
Authorization: Bearer <token>
```

#### Get Product by ID

```http
GET /api/v1/products/1
Authorization: Bearer <token>
```

#### Get Product by SKU

```http
GET /api/v1/products/sku/PROD-001
Authorization: Bearer <token>
```

#### Update Product (Admin/Manager only)

```http
PUT /api/v1/products/1
Authorization: Bearer <token>
Content-Type: application/json

{
  "sku": "PROD-001",
  "name": "Updated Product",
  "price": 119.99,
  "stockQuantity": 150
}
```

#### Delete Product (Admin only)

```http
DELETE /api/v1/products/1
Authorization: Bearer <token>
```

## 🔐 Security Configuration

### JWT Settings

- **Secret Key**: Configured in `application.yml`
- **Token Expiration**: 24 hours (86400000 ms)
- **Token Type**: Bearer
- **Header**: `Authorization: Bearer <token>`

### Role Hierarchy

| Role | Permissions |
|------|-------------|
| **USER** | Can view products |
| **MANAGER** | Can view and manage products (create, update) |
| **ADMIN** | Full access to all endpoints (including delete) |

### Authentication Flow

1. User registers or logs in → Receives JWT token
2. Client includes token in `Authorization` header for subsequent requests
3. `JwtAuthenticationFilter` validates token
4. If valid, user is authenticated and authorized based on role
5. Method-level security enforces role-based access

## 🗄️ Database Schema

### Users Table

| Column | Type | Constraints |
|--------|------|-------------|
| `id` | BIGSERIAL | PRIMARY KEY |
| `username` | VARCHAR(50) | UNIQUE, NOT NULL |
| `email` | VARCHAR(255) | UNIQUE, NOT NULL |
| `password` | VARCHAR(255) | NOT NULL (encrypted) |
| `role` | VARCHAR(20) | NOT NULL (USER, ADMIN, MANAGER) |
| `enabled` | BOOLEAN | NOT NULL, DEFAULT true |
| `account_non_expired` | BOOLEAN | NOT NULL, DEFAULT true |
| `account_non_locked` | BOOLEAN | NOT NULL, DEFAULT true |
| `credentials_non_expired` | BOOLEAN | NOT NULL, DEFAULT true |
| `created_at` | TIMESTAMP | NOT NULL |
| `updated_at` | TIMESTAMP | NOT NULL |

**Indices:**

- `idx_users_username` on `username`
- `idx_users_email` on `email`

### Products Table

| Column | Type | Constraints |
|--------|------|-------------|
| `id` | BIGSERIAL | PRIMARY KEY |
| `sku` | VARCHAR(255) | UNIQUE, NOT NULL |
| `name` | VARCHAR(255) | NOT NULL |
| `price` | DECIMAL(19, 2) | NOT NULL, CHECK (price > 0) |
| `stock_quantity` | INTEGER | NOT NULL, CHECK (stock_quantity >= 0) |

**Indices:**

- `idx_products_sku` on `sku`

## ⚠️ Error Handling

The API uses a global exception handler that returns consistent error responses:

### Validation Error Example

```json
{
  "statusCode": 400,
  "message": "Validation failed",
  "data": {
    "username": "Username is required",
    "email": "Email should be valid"
  }
}
```

### HTTP Status Codes

| Code | Description |
|------|-------------|
| `200` | Success |
| `400` | Bad Request (validation errors) |
| `401` | Unauthorized (invalid credentials or missing token) |
| `403` | Forbidden (insufficient permissions) |
| `404` | Not Found |
| `500` | Internal Server Error |

## 📊 Logging & Monitoring

### Logging Infrastructure

The application implements enterprise-grade logging with:

- **SLF4J + Logback**: Logging framework
- **Logstash Encoder**: JSON-formatted logs for ELK stack
- **MDC (Mapped Diagnostic Context)**: Request context enrichment
- **Async Logging**: Non-blocking I/O for performance

### Key Features

- **Structured JSON logs** exported to Logstash (port 5000)
- **Request tracing** with unique `requestId` per request
- **User activity tracking** with `username` and `userId` in logs
- **Performance monitoring** with request duration tracking
- **Automatic log rotation** (100MB per file, 30-day retention)

### Actuator Endpoints

Access monitoring endpoints at `http://localhost:8080/actuator`:

- `/actuator/health` - Application health status
- `/actuator/metrics` - Available metrics
- `/actuator/prometheus` - Prometheus metrics export
- `/actuator/loggers` - View/modify log levels at runtime

### Example: Change Log Level at Runtime

```bash
# Set log level to DEBUG for debugging
curl -X POST http://localhost:8080/actuator/loggers/com.soloware.pos \
  -H "Content-Type: application/json" \
  -d '{"configuredLevel": "DEBUG"}'

# Reset to INFO
curl -X POST http://localhost:8080/actuator/loggers/com.soloware.pos \
  -H "Content-Type: application/json" \
  -d '{"configuredLevel": "INFO"}'
```

### Log Files

- **Console**: Human-readable logs for development
- **Application Log**: `logs/application.log` (standard format)
- **Logstash Log**: `logs/logstash.json` (JSON format for ELK)

### MDC Context Fields

Every log entry includes:

- `requestId` - Unique request identifier
- `username` - Authenticated user
- `userId` - User ID
- `ipAddress` - Client IP address
- `endpoint` - Request URI
- `httpMethod` - HTTP method
- `httpStatus` - Response status
- `duration` - Request processing time (ms)

### Prometheus/Grafana Integration

The application exposes metrics for Prometheus scraping:

```yaml
# prometheus.yml
scrape_configs:
  - job_name: 'pos-api'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['localhost:8080']
```

For detailed logging documentation, see [LOGGING_GUIDE.md](LOGGING_GUIDE.md).

## 🛠️ Development

### Running Tests

```bash
./mvnw test
```

### Building for Production

```bash
export JAVA_HOME=/usr/lib/jvm/java-25-openjdk-amd64
./mvnw clean package
java -jar target/pos-0.0.1-SNAPSHOT.jar
```

### Database Migrations

Flyway migrations are located in `src/main/resources/db/migration/`:

- **V1__create_products_table.sql**: Creates products table with constraints
- **V2__create_users_table.sql**: Creates users table with default admin user

Migrations run automatically on application startup.

## ✨ Best Practices Implemented

### 1. SOLID Principles

- ✅ **Single Responsibility**: Each class has one clear purpose
- ✅ **Open/Closed**: Service layer uses interfaces for extensibility
- ✅ **Liskov Substitution**: Proper inheritance hierarchy
- ✅ **Interface Segregation**: Focused interfaces
- ✅ **Dependency Inversion**: Dependencies injected via interfaces

### 2. Security Best Practices

- ✅ Passwords encrypted with BCrypt (never stored in plain text)
- ✅ Stateless JWT authentication (scalable, no server-side sessions)
- ✅ Method-level security with `@PreAuthorize`
- ✅ CSRF disabled for API (appropriate for stateless JWT)
- ✅ Token expiration handling
- ✅ Proper exception handling for authentication failures

### 3. Code Quality

- ✅ Lombok for boilerplate reduction
- ✅ Transaction management (`@Transactional`)
- ✅ Proper separation of concerns (layered architecture)
- ✅ DTO pattern for data transfer
- ✅ Repository pattern for data access

### 4. API Design

- ✅ RESTful endpoints
- ✅ Consistent response format (`ApiResponse` wrapper)
- ✅ Comprehensive API documentation (Swagger/OpenAPI)
- ✅ Versioned API paths (`/api/v1/...`)
- ✅ Proper HTTP status codes

### 5. Validation

- ✅ Jakarta Bean Validation on DTOs
- ✅ Database-level constraints
- ✅ Global exception handling
- ✅ Meaningful error messages

## 📖 Reference Documentation

- [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
- [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/4.0.0/maven-plugin)
- [Spring Data JPA](https://docs.spring.io/spring-boot/4.0.0/reference/data/sql.html#data.sql.jpa-and-spring-data)
- [Spring Security](https://docs.spring.io/spring-boot/4.0.0/reference/web/spring-security.html)
- [Flyway Migration](https://docs.spring.io/spring-boot/4.0.0/how-to/data-initialization.html#howto.data-initialization.migration-tool.flyway)
- [Spring Boot DevTools](https://docs.spring.io/spring-boot/4.0.0/reference/using/devtools.html)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the Apache License 2.0.

## 💬 Support

For support, email <dev@soloware.id> or visit <https://soloware.id>
