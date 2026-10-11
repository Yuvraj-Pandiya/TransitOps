# TransitOps - Smart Transport Operations Platform (Backend)

A microservices-based backend for fleet and transport operations. It digitizes vehicle, driver, trip, maintenance and expense management, enforces business rules on the server, and secures every API with Keycloak (OAuth2 / JWT) and role-based access control.

## Tech Stack

| Area | Technology |
|---|---|
| Language / Framework | Java, Spring Boot |
| Microservices | Spring Cloud: Eureka (discovery), OpenFeign (inter-service calls), Spring Cloud Gateway |
| Security | Keycloak, OAuth2 / OpenID Connect, JWT, Spring Security (Resource Server) |
| Database | PostgreSql (database per service) |
| API Testing | Postman |

## Architecture

```
                 Client / Postman (Bearer JWT from Keycloak)
                                  |
                                  v
                 +-------------------------------+
                 |  API Gateway (port 8765)      |
                 |  validates JWT, checks roles, |
                 |  routes /api/** by service    |
                 +---------------+---------------+
                                 |  lb:// (service names via Eureka)
     +------------+------------+-+----------+-------------+
     v            v            v            v             v
 vehicle-     driver-       trip-      maintenance-    expense-
 service      service      service       service       service
                              |  OpenFeign
                              +--> vehicle-service, driver-service

          Eureka Server  <-- every service registers here
          Keycloak (port 8180, realm: transitops-realm) issues the tokens
```

### Services

| Service | Responsibility | Calls (OpenFeign) |
|---|---|---|
| `eureka-server` | Service registry; all services register here | - |
| `api-gateway` | Single entry point, JWT validation, role-based route protection, CORS | routes to all services |
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

Users and roles are managed by Keycloak, not by a service table.

Because each service has its own database, these links across services are logical references by ID (for example `vehicleId` in Trips), validated through Feign calls and not by database foreign keys.

## Security

Authentication and authorization are handled by Keycloak and enforced at the API Gateway.

1. The client logs in at Keycloak (realm `transitops-realm`) and receives a signed JWT.
2. The client sends `Authorization: Bearer <token>` to the gateway.
3. The gateway validates the token against Keycloak, reads the realm roles, and allows or rejects the request (401 for no/invalid token, 403 for a valid token without the needed role).

### Roles and access

R = read (GET), W = create / update / delete / actions. `ADMIN` has full access.

| Module | FLEET_MANAGER | DISPATCHER | SAFETY_OFFICER | FINANCIAL_ANALYST |
|---|---|---|---|---|
| Vehicles | W | R | R | R |
| Drivers | R | R | W | R |
| Trips | R | W | R | R |
| Maintenances | W | - | - | R |
| Expenses | R | - | - | W |
| Reports | R | - | - | R |

### Keycloak clients

| Client | Type | Purpose |
|---|---|---|
| `transitops-frontend-client` | Public, Authorization Code + PKCE | Login for apps and Postman |
| `transitops-gateway-client` | Confidential | Identity for the gateway |

## API Overview

All requests go through the gateway at `http://localhost:8765`. The gateway removes the `/api` prefix before forwarding.

| Prefix | Service | Main operations |
|---|---|---|
| `/api/vehicles` | vehicle-service | create, get by id, get all, update, delete, update status |
| `/api/drivers` | driver-service | create, get by id, get all, update, delete |
| `/api/trips` | trip-service | create, get by id, dispatch, complete, cancel, delete |
| `/api/maintenances` | maintenance-service | create, find by id, records by vehicle, close, update, delete |
| `/api/expenses` | expense-service | create, get by id, by vehicle, by type, total amount by vehicle, update, delete |
| `/api/reports` | report-service | planned |

## Running Locally

### Prerequisites
- Java (version used by the project) and Maven
- PostgreSql running locally
- Keycloak running on port 8180 with realm `transitops-realm`, the two clients, the roles and the test users configured

### Start order
1. PostgreSQL and Keycloak
2. `eureka-server` (dashboard at `http://localhost:8761`)
3. `vehicle-service`, `driver-service`, `trip-service`, `maintenance-service`, `expense-service`
4. `api-gateway` (port 8765)

Each service: `cd <service-folder>` then `mvn spring-boot:run`. Check the Eureka dashboard to confirm all services are registered.

### Getting a token (Postman)
- Auth type: OAuth 2.0, grant type Authorization Code (With PKCE), challenge method SHA-256
- Auth URL: `http://localhost:8180/realms/transitops-realm/protocol/openid-connect/auth`
- Token URL: `http://localhost:8180/realms/transitops-realm/protocol/openid-connect/token`
- Client ID: `transitops-frontend-client`, callback `https://oauth.pstmn.io/v1/callback`

### Test users

| Username | Role |
|---|---|
| admin_user | ADMIN |
| manager_user | FLEET_MANAGER |
| dispatcher_user | DISPATCHER |
| safety_user | SAFETY_OFFICER |
| finance_user | FINANCIAL_ANALYST |
| norole_user | none (expects 403) |

Passwords are for local testing only and are not stored in this repository.

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
- [x] Vehicle, driver, trip, maintenance and expense services
- [x] API Gateway with explicit routes and CORS
- [x] Keycloak authentication and role-based access control at the gateway
- [ ] report-service (dashboard KPIs, fuel efficiency, operational cost, ROI, CSV export)
- [ ] Resilience4j circuit breaker and fallbacks
- [ ] Kafka events (for example `trip.completed` consumed by report-service)
- [ ] Distributed tracing and centralized logging
