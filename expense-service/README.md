# TransitOps — Expense Service

Manages fuel consumption logs and operational expenses to calculate the total operating cost per vehicle.

## Overview
- **Port:** `8085`
- **Eureka Name:** `expense-service`
- **Database:** PostgreSQL (`transitops_expense`)
- **Key Responsibilities:**
  - Record fuel fill-ups (liters, cost per liter, total cost, odometer reading).
  - Record miscellaneous expenses (`Toll`, `Insurance`, `Permits`, etc.).
  - Associates expenditures with vehicles and trips.
  - Aggregates total operational cost (`Fuel + Other Expenses + Maintenance`) by calling `maintenance-service` via **OpenFeign**.

## OpenFeign Clients
- `VehicleClient` &rarr; targets `vehicle-service` to verify vehicle existence.
- `MaintenanceClient` &rarr; targets `maintenance-service` to retrieve maintenance costs for a vehicle.

## Key Endpoints
- `POST /expenses` — Create a general expense entry
- `POST /expenses/fuel` — Log a fuel fill-up
- `GET /expenses` — List expenses (filterable by `vehicleId`, `category`, `tripId`)
- `GET /expenses/fuel` — List fuel logs
- `GET /expenses/{id}` — Get expense by ID
- `GET /expenses/vehicle/{vehicleId}/total-cost` — Compute total operational cost for a vehicle
- `PUT /expenses/{id}` — Update expense details
- `DELETE /expenses/{id}` — Delete expense record

## Running the Service
```bash
./mvnw spring-boot:run
```
