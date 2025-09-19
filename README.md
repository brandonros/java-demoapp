# Spring Boot Microservice

A minimal Spring Boot HTTP microservice with a simple ping endpoint.

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

## Testing the Endpoint

Once the application is running on port 8080:

```bash
curl http://localhost:8080/ping
```

Expected response: `pong`

## Building JAR

```bash
./mvnw clean package
```

Run the JAR:
```bash
java -jar target/demo-1.0.0.jar
```