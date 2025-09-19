# java-demoapp

A minimal Spring Boot HTTP microservice with MS SQL Server integration, JSON logging, and Swagger documentation.

## Prerequisites

- Java 24 or higher
- Maven (optional - project includes Maven wrapper)
- MS SQL Server (or Azure SQL)

## Running the Application

### Using Maven Wrapper
```bash
./mvnw spring-boot:run
```

### Using Maven
```bash
mvn spring-boot:run
```

## Features

- **MS SQL Server Integration**: Database connectivity with HikariCP connection pooling
- **JSON Logging**: All logs are output in JSON format for better parsing and monitoring
- **Swagger/OpenAPI**: Interactive API documentation available at `/swagger-ui`
- **Kubernetes Health Probes**: Liveness and readiness endpoints for container orchestration

## API Endpoints

### Health Checks

#### Legacy Ping
```bash
curl http://localhost:8080/ping
```

#### Kubernetes Liveness Probe
```bash
curl http://localhost:8080/healthz/live
```

#### Kubernetes Readiness Probe
```bash
curl http://localhost:8080/healthz/ready
```

### Get Account by UUID
```bash
curl http://localhost:8080/api/accounts/{uuid}
```

### Swagger UI
Open in browser: http://localhost:8080/swagger-ui

### OpenAPI Documentation
```bash
curl http://localhost:8080/api-docs
```

## Database Setup

1. Update database connection in `application.properties`
2. Create stored procedure `sp_GetAccountByUuid` in your MS SQL database

## Testing

### Run Tests
```bash
./mvnw test
```

### Generate Coverage Report
```bash
./mvnw clean test
# Coverage report: target/site/jacoco/index.html
```

### Verify Coverage Thresholds
```bash
./mvnw clean verify
# Enforces minimum 80% code coverage
```

## Building JAR

```bash
./mvnw clean package
```

Run the JAR:
```bash
java -jar target/demo-1.0.0.jar
```