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
curl http://localhost:8080/api/accounts/550e8400-e29b-41d4-a716-446655440000
```
Calls stored procedure `sp_GetAccountByUuid` and returns account data

### Swagger UI
Open in browser: http://localhost:8080/swagger-ui

### OpenAPI Documentation
```bash
curl http://localhost:8080/api-docs
```

## Database Configuration

Update `src/main/resources/application.properties` with your database connection:
```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=mydb
spring.datasource.username=sa
spring.datasource.password=YourPassword
```

### Required Stored Procedure

Create this stored procedure in your MS SQL database:
```sql
CREATE PROCEDURE sp_GetAccountByUuid
    @uuid NVARCHAR(36)
AS
BEGIN
    SELECT * FROM Accounts WHERE account_uuid = @uuid
END
```

## Building JAR

```bash
./mvnw clean package
```

Run the JAR:
```bash
java -jar target/demo-1.0.0.jar
```