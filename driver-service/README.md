# TransitOps — Driver Service

Manages driver profiles, license verification, safety scores, and regulatory compliance.

## Overview
- **Port:** `8082`
- **Eureka Name:** `driver-service`
- **Database:** PostgreSQL (`transitops_driver`)
- **Key Responsibilities:**
  - Driver onboarding and profile maintenance (CRUD).
  - License validation and expiry monitoring.
  - Driver status lifecycle: `Available`, `On Trip`, `Off Duty`, `Suspended`.
  - Exposes eligibility check endpoints for `trip-service` dispatch verification.

## Key Endpoints
- `POST /drivers` — Register a new driver
- `GET /drivers` — List drivers (supports filters by `status`, `search`)
- `GET /drivers/{id}` — Get driver profile and safety score
- `GET /drivers/expiring` — Get drivers with licenses expiring within warning thresholds
- `PUT /drivers/{id}` — Update driver details
- `PATCH /drivers/{id}/status` — Update driver status
- `DELETE /drivers/{id}` — Delete driver record

## Running the Service
```bash
./mvnw spring-boot:run
```
