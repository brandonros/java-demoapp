# Spring Boot Microservice

A minimal Spring Boot HTTP microservice with MS SQL Server integration, JSON logging, and Swagger documentation.

## Prerequisites

- Java 17 or higher
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

## API Endpoints

### Health Check
```bash
curl http://localhost:8080/ping
```
Expected response: `pong`

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

## Building JAR

```bash
./mvnw clean package
```

Run the JAR:
```bash
java -jar target/demo-1.0.0.jar
```