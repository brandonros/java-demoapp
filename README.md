# Spring Boot Microservice

A minimal Spring Boot HTTP microservice with a simple ping endpoint, JSON logging, and Swagger documentation.

## Prerequisites

- Java 17 or higher
- Maven (optional - project includes Maven wrapper)

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

- **JSON Logging**: All logs are output in JSON format for better parsing and monitoring
- **Swagger/OpenAPI**: Interactive API documentation available at `/swagger-ui`

## API Endpoints

### Health Check
```bash
curl http://localhost:8080/ping
```
Expected response: `pong`

### Swagger UI
Open in browser: http://localhost:8080/swagger-ui

### OpenAPI Documentation
```bash
curl http://localhost:8080/api-docs
```

## Building JAR

```bash
./mvnw clean package
```

Run the JAR:
```bash
java -jar target/demo-1.0.0.jar
```