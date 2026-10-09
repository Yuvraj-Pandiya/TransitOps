# TransitOps — Service Registry (Eureka Server)

The central discovery server for the TransitOps microservices ecosystem powered by Spring Cloud Netflix Eureka.

## Overview
- **Port:** `8761`
- **Dashboard URL:** `http://localhost:8761`
- **Role:** Service Discovery Server
- **Responsibilities:**
  - Registers all microservices dynamically on startup.
  - Monitors instances with continuous health heartbeats.
  - Provides dynamic host and port discovery for Spring Cloud Gateway and OpenFeign clients.

## Configuration Highlights (`application.properties`)
```properties
server.port=8761
spring.application.name=service-registry
eureka.client.register-with-eureka=false
eureka.client.fetch-registry=false
```

## Running the Service
```bash
./mvnw spring-boot:run
```
Once started, verify the Eureka web dashboard at `http://localhost:8761`.
