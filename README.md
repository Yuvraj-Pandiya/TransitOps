# TransitOps - Smart Transport Operations Platform (Backend)

A microservices-based backend for fleet and transport operations. It digitizes vehicle, driver, trip, maintenance, and expense management, enforces business rules on the server, and supports role-based access control and Keycloak authentication at the API Gateway.

## Tech Stack

| Area | Technology |
|---|---|
| Language / Framework | Java, Spring Boot |
| Microservices | Spring Cloud: Eureka (discovery), OpenFeign (inter-service calls), Spring Cloud Gateway |
| Security | Spring Security & Keycloak (OAuth2 / JWT) |
| Database | PostgreSQL (database per service) |
| API Testing | Postman (collection included) |

## Architecture

```
                       Client / Postman
                              |
                              v
             +---------------------------------+
             |   API Gateway (port 8765)       |
             |   routes /api/** by service     |
             |   (role-based security / JWT)   |
             +----------------+----------------+
                              |  lb:// (service names via Eureka)
      +------------+------------+-+----------+-------------+
      v            v            v            v             v
  vehicle-     driver-       trip-      maintenance-    expense-
  service      service      service       service       service
                               |  OpenFeign
                               +--> vehicle-service, driver-service

           Eureka Server  <-- every service registers here
           Keycloak (port 8180, in progress) <-- user authentication
```

### Services

| Service | Responsibility | Calls (OpenFeign) |
|---|---|---|
| `eureka-server` | Service registry; all services register here | - |
| `api-gateway` | Single entry point, request routing, role-based security, CORS | routes to all services |
| `vehicle-service` | Vehicle registry, unique registration number, status (Available, On Trip, In Shop, Retired) | - |
| `driver-service` | Driver profiles, license expiry, safety score, status (Available, On Trip, Off Duty, Suspended) | - |
| `trip-service` | Trip lifecycle and dispatch rules | vehicle-service, driver-service |
| `maintenance-service` | Maintenance records; puts a vehicle In Shop and restores it on close | vehicle-service |
| `expense-service` | Fuel logs and other expenses; total cost per vehicle | vehicle-service |
| `report-service` | Planned: dashboard KPIs, fuel efficiency, operational cost, vehicle ROI, CSV export | all services |

Each service owns its own data. A service gets another service's data through a REST/Feign call, never by reading its database.

## Business Rules (enforced in the backend)

- Vehicle registration number must be unique.
- Retired or In Shop vehicles can never be selected for dispatch.
- Drivers with an expired license or Suspended status cannot be assigned to a trip.
- A vehicle or driver already On Trip cannot be assigned to another trip.
- Cargo weight must not exceed the vehicle's maximum load capacity.
- Trip lifecycle: Draft, Dispatched, Completed, Cancelled.
- Dispatching a trip sets both vehicle and driver to On Trip.
- Completing a trip sets both back to Available and records final odometer and fuel consumed.
- Cancelling a dispatched trip restores both to Available.
- An active maintenance record sets the vehicle to In Shop; closing it restores Available (unless Retired).
- Maintenance and expense records must reference an existing vehicle.

## Database Design

Entities: Vehicles, Drivers, Trips, Maintenances, Expenses and Reports.

| Relationship | Type |
|---|---|
| Vehicle - Trip | 1 : N |
| Driver - Trip | 1 : N |
| Vehicle - Maintenance | 1 : N |
| Vehicle - Expense | 1 : N |
| Trip - Expense | 1 : N (optional) |

User identity and credentials are intentionally decoupled from business entities, intended for management by Keycloak.

Because each service has its own database, these links across services are logical references by ID (for example `vehicleId` in Trips), validated through Feign calls and not by database foreign keys.

## Security & Access Control

Security is handled at the **API Gateway** level using Spring Security and Keycloak:

- **Keycloak (Authentication):** Acts as the centralized identity provider running on port `8180` to issue access tokens (JWTs). *(Note: Keycloak integration is currently in basic setup / work in progress).*
- **API Gateway (Authorization):** Checks the user's role on incoming `/api/**` requests and allows or blocks the action based on predefined permissions.

### Roles and Access Matrix

| Module | FLEET_MANAGER | DISPATCHER | SAFETY_OFFICER | FINANCIAL_ANALYST |
|---|---|---|---|---|
| Vehicles | W | R | R | R |
| Drivers | R | R | W | R |
| Trips | R | W | R | R |
| Maintenances | W | - | - | R |
| Expenses | R | - | - | W |
| Reports | R | - | - | R |

> **Legend:** `R` = Read (`GET`), `W` = Write / Create / Update / Delete (`POST`, `PUT`, `DELETE`). The `ADMIN` role has full access across all modules.

## API Overview

All routed requests go through the API Gateway at `http://localhost:8765`. The gateway removes the `/api` prefix before forwarding to the respective microservice:

| Prefix | Service | Main operations |
|---|---|---|
| `/api/vehicles` | vehicle-service | create, get by id, get all, update, delete, update status |
| `/api/drivers` | driver-service | create, get by id, get all, update, delete |
| `/api/trips` | trip-service | create, get by id, dispatch, complete, cancel, delete |
| `/api/maintenances` | maintenance-service | create, find by id, records by vehicle, close, update, delete |
| `/api/expenses` | expense-service | create, get by id, by vehicle, by type, total amount by vehicle, update, delete |
| `/api/reports` | report-service | planned |

## Postman API Collection & Testing

A complete Postman collection containing pre-configured requests and JSON payloads for every service and gateway route is provided in this repository.

### File Location
```
postman/TransitOps.postman_collection.json
```

### How to Import into Postman
1. Open the **Postman** application.
2. Click the **Import** button in the top left corner of the workspace.
3. Select or drag-and-drop the file: `postman/TransitOps.postman_collection.json`.
4. Click **Import**. The `TransitOps` collection will now be available in your Postman collection list.

### Collection Folders & Usage

The collection is organized into modular folders matching each service:

| Folder | Target URL | Description |
|---|---|---|
| **`vehicle-service`** | `http://localhost:8080/vehicles` | Direct CRUD operations: create vehicle, get by ID, update vehicle details, delete. |
| **`driver-service`** | `http://localhost:8081/drivers` | Direct driver management: create driver, view profiles, update status, delete. |
| **`trip-service`** | `http://localhost:8082/trips` | Direct trip lifecycle: draft trip, dispatch, complete with odometer/fuel, cancel. |
| **`maintenance-service`** | `http://localhost:8083/maintenances` | Direct maintenance workflows: schedule maintenance, view by vehicle, close maintenance. |
| **`expense-service`** | `http://localhost:8084/expenses` | Direct expense tracking: log fuel/toll, get expenses by vehicle, query totals. |
| **`api-gateway`** | `http://localhost:8765/api/...` | Tests all operations routed through the unified gateway entry point. |

### Testing Modes
- **Direct Service Testing:** Start `eureka-server` and the specific service you are working on, then run the requests in that service's folder (e.g., test vehicle APIs directly on port `8080`).
- **Unified Gateway Testing:** Start all services and `api-gateway` (port `8765`), then trigger the endpoints under the `api-gateway` folder.

## Running Locally

### Prerequisites
- Java (JDK 21) and Maven
- PostgreSQL running locally (configured for each service database)
- *(Optional / In Progress)* Keycloak running on port 8180 with realm `transitops-realm`

### Start Order
1. PostgreSQL
2. `eureka-server` (discovery dashboard available at `http://localhost:8761`)
3. Individual microservices:
   - `vehicle-service` (port 8080)
   - `driver-service` (port 8081)
   - `trip-service` (port 8082)
   - `maintenance-service` (port 8083)
   - `expense-service` (port 8084)
4. `api-gateway` (port 8765)

To start any service:
```bash
cd <service-folder>
mvn spring-boot:run
```
Open `http://localhost:8761` to verify all services are registered and active in Eureka.

## Example Workflow

1. Register vehicle `Van-05` with 500 kg capacity (Available).
2. Register driver `Alex` with a valid license.
3. Create a trip with 450 kg cargo (Draft).
4. Dispatch: capacity and eligibility are verified, and vehicle and driver become On Trip.
5. Complete the trip with final odometer and fuel: both become Available.
6. Add a fuel expense for the vehicle.
7. Create a maintenance record (oil change): the vehicle becomes In Shop and disappears from dispatch.

## Roadmap

- [x] Eureka service registry
- [x] Vehicle, driver, trip, maintenance, and expense services
- [x] API Gateway with explicit routes and CORS
- [ ] Finalize Keycloak realm configuration and security hardening (in progress)
- [ ] report-service (dashboard KPIs, fuel efficiency, operational cost, ROI, CSV export)
- [ ] Resilience4j circuit breaker and fallbacks
- [ ] Kafka events (for example `trip.completed` consumed by report-service)
- [ ] Distributed tracing and centralized logging
