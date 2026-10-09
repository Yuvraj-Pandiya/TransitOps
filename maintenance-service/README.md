# TransitOps — Maintenance Service

Manages vehicle servicing, repairs, workshops, and preventive maintenance records.

## Overview
- **Port:** `8084`
- **Eureka Name:** `maintenance-service`
- **Database:** PostgreSQL (`transitops_maintenance`)
- **Key Responsibilities:**
  - Vehicle maintenance logging (`Oil Change`, `Tire Replacement`, `Brake Service`, `Engine Overhaul`, etc.).
  - Interacts with `vehicle-service` via **OpenFeign**:
    - When maintenance is created (`Active`), automatically marks vehicle as `In Shop` (removing it from dispatch eligibility).
    - When maintenance is closed (`Closed`), automatically restores vehicle to `Available` (unless retired).
  - Tracks service costs for vehicle operational expenditure calculation.

## OpenFeign Clients
- `VehicleClient` &rarr; targets `vehicle-service` to update vehicle status to `In Shop` or `Available`.

## Key Endpoints
- `POST /maintenances` — Log a new maintenance service (sets vehicle to `In Shop`)
- `GET /maintenances` — List maintenance records (filterable by `status`, `vehicleId`)
- `GET /maintenances/{id}` — Get maintenance record details
- `PUT /maintenances/{id}/close` — Complete and close maintenance (restores vehicle to `Available`)
- `PUT /maintenances/{id}` — Update maintenance record details
- `DELETE /maintenances/{id}` — Delete maintenance record

## Running the Service
```bash
./mvnw spring-boot:run
```
