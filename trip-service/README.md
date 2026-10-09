# TransitOps — Trip Service

The core operations orchestrator that coordinates deliveries, assigns fleet assets, and manages the complete trip lifecycle.

## Overview
- **Port:** `8083`
- **Eureka Name:** `trip-service`
- **Database:** PostgreSQL (`transitops_trip`)
- **Key Responsibilities:**
  - Trip lifecycle state machine: `Draft` &rarr; `Dispatched` &rarr; `Completed` &rarr; `Cancelled`.
  - Integrates with `vehicle-service`, `driver-service`, and `expense-service` via **OpenFeign**.
  - Enforces operational validation: cargo weight &le; vehicle capacity, driver license validity, and `Available` asset status.
  - Automatically transitions vehicle and driver to `On Trip` on dispatch, and returns them to `Available` on completion.
  - Sends fuel consumption logs to `expense-service` upon trip completion.

## OpenFeign Clients
- `VehicleClient` &rarr; targets `vehicle-service` for availability checks and status updates.
- `DriverClient` &rarr; targets `driver-service` for license validation and status updates.
- `ExpenseClient` &rarr; targets `expense-service` to log fuel fill-ups on trip completion.

## Key Endpoints
- `POST /trips` — Create a new trip in `Draft` status
- `GET /trips` — List trips (filterable by status)
- `GET /trips/{id}` — Get trip details
- `POST /trips/{id}/dispatch` — Validate and dispatch a trip
- `POST /trips/{id}/complete` — Complete trip with final odometer reading and fuel consumption
- `POST /trips/{id}/cancel` — Cancel a trip and restore assigned assets to `Available`
- `DELETE /trips/{id}` — Remove a draft trip

## Running the Service
```bash
./mvnw spring-boot:run
```
