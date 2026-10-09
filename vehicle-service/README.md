# TransitOps — Vehicle Service

The master registry for all fleet assets and availability status management.

## Overview
- **Port:** `8081`
- **Eureka Name:** `vehicle-service`
- **Database:** PostgreSQL (`transitops_vehicle`)
- **Key Responsibilities:**
  - Vehicle lifecycle management (CRUD).
  - Enforces unique registration numbers.
  - Maintains vehicle statuses: `Available`, `On Trip`, `In Shop`, `Retired`.
  - Exposes endpoints for capacity checks and odometer updates called by `trip-service` and `maintenance-service`.

## Key Endpoints
- `POST /vehicles` — Register a new vehicle
- `GET /vehicles` — List vehicles (supports filters by `status`, `type`, `search`)
- `GET /vehicles/{id}` — Get vehicle details
- `PUT /vehicles/{id}` — Update vehicle specifications
- `PATCH /vehicles/{id}/status` — Update vehicle availability status (`Available`, `On Trip`, `In Shop`, `Retired`)
- `DELETE /vehicles/{id}` — Remove a vehicle

## Running the Service
```bash
./mvnw spring-boot:run
```
