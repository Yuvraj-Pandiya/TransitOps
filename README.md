# TransitOps — Smart Transport Operations Platform

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2024.x-blue.svg)](https://spring.io/projects/spring-cloud)
[![Keycloak](https://img.shields.io/badge/Identity-Keycloak%20OAuth2%20%2F%20PKCE-red.svg)](https://www.keycloak.org/)
[![React](https://img.shields.io/badge/Frontend-React%2019%20%2B%20Vite-61dafb.svg)](https://react.dev/)
[![PostgreSQL](https://img.shields.io/badge/Database-PostgreSQL-336791.svg)](https://www.postgresql.org/)

TransitOps is an enterprise-grade logistics and fleet management platform engineered on a **Distributed Microservices Architecture**. It replaces fragmented logbooks and spreadsheets with automated fleet availability tracking, driver compliance enforcement, trip dispatching state machines, vehicle maintenance schedules, and operational expense tracking.

---

## 1. System Architecture & Design Philosophy

```
                              +---------------------------------------+
                              |         Keycloak IAM Server           |
                              |  (OAuth2 / OpenID Connect + PKCE)     |
                              |     http://localhost:8180             |
                              +-------------------+-------------------+
                                                  ^
                                                  | Token / SSO
                                                  v
+-----------------------------------+   HTTP Bearer JWT   +------------------------------------+
|         React 19 Frontend         | ------------------> |        Spring Cloud Gateway        |
|      (Vite + keycloak-js)         |                     |   (Reactive WebFlux Resource Svr)  |
|      http://localhost:5173        |                     |        http://localhost:8765       |
+-----------------------------------+                     +-----------------+------------------+
                                                                            |
                                                      +---------------------+--------------------+
                                                      | Eureka Discovery Lookup (lb://<service>) |
                                                      v                                          v
                                   +--------------------------------------+   +--------------------------------------+
                                   |       Netflix Eureka Server          |   |           Database Layer             |
                                   |         (service-registry)           |   |       (Database-per-Service)         |
                                   |        http://localhost:8761         |   |             PostgreSQL               |
                                   +------------------+-------------------+   +--------------------------------------+
                                                      |
              +-------------------+-------------------+-------------------+-------------------+
              |                   |                   |                   |                   |
              v                   v                   v                   v                   v
     +-----------------+ +-----------------+ +-----------------+ +-----------------+ +-----------------+
     | vehicle-service | | driver-service  | |  trip-service   | |  maintenance-   | | expense-service |
     |   Port: 8081    | |   Port: 8082    | |   Port: 8083    | |     service     | |   Port: 8085    |
     |                 | |                 | |                 | |   Port: 8084    | |                 |
     +--------+--------+ +--------+--------+ +---+----+----+---+ +--------+--------+ +--------+--------+
              ^                   ^              |    |    |              |                   ^
              |                   +--------------+    |    +--------------|-------------------+
              |                       OpenFeign       |       OpenFeign   |       OpenFeign   |
              +---------------------------------------+-------------------+-------------------+
                                     OpenFeign Status & Verification
```

### Architectural Decisions & Modernizations

1. **Enterprise Identity (Keycloak vs. Custom Auth Service):**
   - The initial course specification called for a bespoke `auth-service` issuing JWTs.
   - In production microservices, building a custom auth service introduces single-point-of-failure vulnerabilities, duplicate token validation code, and tightly couples user storage with application databases.
   - **Modernization:** TransitOps adopts **Keycloak (v26)** as a dedicated Identity & Access Management (IAM) provider.
     - **Frontend:** Authenticates directly with Keycloak using **Authorization Code Flow with PKCE (`S256`)** and `onLoad: 'login-required'`. Tokens are kept strictly in memory.
     - **API Gateway:** Configured as a **Reactive OAuth2 Resource Server**, inspecting the Keycloak public key and translating `realm_access.roles` into Spring Security authorities (`ROLE_<NAME>`).
     - **Backend Services:** Focused entirely on domain logic, shielded by the Gateway.
2. **Spring Cloud Gateway (WebFlux) vs. Blocking MVC:**
   - The Gateway uses non-blocking **Spring WebFlux** (Netty) for high-throughput, low-latency reverse proxying with `StripPrefix=1`.
3. **Database-per-Service:**
   - Each business microservice owns its private PostgreSQL database (`transitops_vehicle`, `transitops_driver`, `transitops_trip`, `transitops_maintenance`, `transitops_expense`). No cross-database queries or shared tables exist.
4. **Declarative Service-to-Service Communication (OpenFeign + Eureka):**
   - Services discover each other by logical application name through Eureka without hardcoded IP addresses or ports.

---

## 2. Project Component Status

| Service Name | Port | Type | Responsibilities & Status |
|---|---|---|---|
| **`service-registry`** | `8761` | Infrastructure | **Completed.** Netflix Eureka Discovery Server. Registers and heartbeats all microservices. |
| **`api-gateway`** | `8765` | Infrastructure | **Completed.** Spring Cloud Gateway (WebFlux). Keycloak JWT Resource Server, CORS handling, RBAC route security. |
| **`vehicle-service`** | `8081` | Business Domain | **Completed.** Vehicle registry, status state machine (`Available`, `On Trip`, `In Shop`, `Retired`), capacity validation. |
| **`driver-service`** | `8082` | Business Domain | **Completed.** Driver licensing, safety ratings, status rules (`Available`, `On Trip`, `Off Duty`, `Suspended`), expiry audits. |
| **`trip-service`** | `8083` | Business Domain | **Completed.** Core trip orchestrator (`Draft` &rarr; `Dispatched` &rarr; `Completed` &rarr; `Cancelled`). Feign integration with vehicles, drivers, and expenses. |
| **`maintenance-service`** | `8084` | Business Domain | **Completed.** Maintenance logging (`Oil Change`, `Brake Service`, etc.). Feign updates `vehicle-service` to `In Shop`, and back to `Available` on completion. |
| **`expense-service`** | `8085` | Business Domain | **Completed.** Fuel logs & operational expense tracking. Calculates total operating cost per vehicle via Feign to maintenance. |
| **`report-service`** | `8086` | Business Domain | **In Progress.** Aggregation service for Dashboard KPIs, Fuel Efficiency, Operational Cost, and ROI metrics. *(Upcoming milestone)* |
| **`frontend`** | `5173` | UI Client | **Completed.** React 19 + Vite dashboard with dark mode, Keycloak PKCE auth, Axios auto-refresh interceptor, dynamic RBAC UI. |

---

## 3. Role-Based Access Control (RBAC)

TransitOps enforces access controls both at the **API Gateway** (network barrier) and in the **React Frontend** (UI visibility).

### Roles & Responsibilities

| Role | Domain Scope & Responsibilities |
|---|---|
| **`ADMIN`** | Universal operational access. Full read and write permissions across all services and configurations. |
| **`FLEET_MANAGER`** | Oversees vehicle asset lifecycle, maintenance service schedules, and operational readiness. |
| **`DISPATCHER`** | Plans routes, assigns eligible vehicles & drivers, dispatches trips, and completes deliveries. |
| **`SAFETY_OFFICER`** | Audits driver compliance, valid license verification, safety scores, and driver status. |
| **`FINANCIAL_ANALYST`** | Tracks fuel expenditure, operational overhead, vehicle total cost of ownership, and ROI analytics. |

### Gateway Route Security Matrix

```
/api/vehicles/**      --> GET: [ADMIN, FLEET_MANAGER, DISPATCHER, SAFETY_OFFICER, FINANCIAL_ANALYST]
                      --> POST/PUT/DELETE: [ADMIN, FLEET_MANAGER]

/api/drivers/**       --> GET: [ADMIN, FLEET_MANAGER, DISPATCHER, SAFETY_OFFICER, FINANCIAL_ANALYST]
                      --> POST/PUT/DELETE: [ADMIN, SAFETY_OFFICER]

/api/trips/**         --> GET: [ADMIN, FLEET_MANAGER, DISPATCHER, SAFETY_OFFICER, FINANCIAL_ANALYST]
                      --> POST/PUT/DELETE: [ADMIN, DISPATCHER]

/api/maintenances/**  --> GET: [ADMIN, FLEET_MANAGER, FINANCIAL_ANALYST]
                      --> POST/PUT/DELETE: [ADMIN, FLEET_MANAGER]

/api/expenses/**      --> GET: [ADMIN, FLEET_MANAGER, FINANCIAL_ANALYST]
                      --> POST/PUT/DELETE: [ADMIN, FINANCIAL_ANALYST]

/api/reports/**       --> GET: [ADMIN, FLEET_MANAGER, FINANCIAL_ANALYST]
```

---

## 4. Inter-Service Communication Matrix (OpenFeign)

Services never communicate via hardcoded hostnames or ports. They rely on **OpenFeign** clients coupled with **Eureka Discovery**:

| Calling Service | Target Service (Eureka Name) | Triggering Action / Reason |
|---|---|---|
| `trip-service` | `vehicle-service` | Verify vehicle existence, capacity check (`cargoWeight <= maxLoadCapacity`), status validation, status transition to `On Trip` / `Available`. |
| `trip-service` | `driver-service` | Verify driver existence, license expiration check, status check, status transition to `On Trip` / `Available`. |
| `trip-service` | `expense-service` | Record fuel consumption log when a trip is marked `Completed`. |
| `maintenance-service` | `vehicle-service` | Verify vehicle existence; set status to `In Shop` on active service, restore to `Available` on service close. |
| `expense-service` | `vehicle-service` | Validate vehicle existence before logging fuel/expenses. |
| `expense-service` | `maintenance-service` | Fetch maintenance costs to aggregate total operational expenses per vehicle. |
| `report-service` *(upcoming)* | `vehicle-service`, `driver-service`, `trip-service`, `expense-service` | Aggregate multi-service metrics for ROI, fuel efficiency, and fleet utilization. |

---

## 5. End-to-End Business Flow

### Scenario: Trip Lifecycle & Maintenance Cycle
1. **Fleet Asset Registration:**
   - Fleet Manager adds vehicle `TRK-001` (Max Capacity: `5000 kg`, Status: `Available`) in `vehicle-service`.
   - Safety Officer adds driver `Alex` (License valid, Safety Score: `95`, Status: `Available`) in `driver-service`.
2. **Trip Creation & Eligibility Check:**
   - Dispatcher creates a trip from Mumbai to Pune with cargo weight `4200 kg`.
   - `trip-service` saves the trip in `Draft` state.
3. **Trip Dispatch:**
   - Dispatcher triggers Dispatch.
   - `trip-service` invokes `vehicle-service` & `driver-service` via Feign.
   - Validates:
     - `cargoWeight (4200) <= maxLoadCapacity (5000)`
     - Vehicle is `Available` (not `In Shop`, `On Trip`, or `Retired`)
     - Driver is `Available` with non-expired license
   - State transition: Vehicle & Driver become `On Trip`. Trip state becomes `Dispatched`.
4. **Trip Completion & Fuel Logging:**
   - Dispatcher completes trip with final odometer reading and fuel consumed (`120 L`).
   - `trip-service` calls `expense-service` via Feign to automatically log the fuel record.
   - Vehicle and Driver status return to `Available`.
5. **Maintenance Scheduling:**
   - Fleet Manager logs an Oil Change for `TRK-001` in `maintenance-service`.
   - `maintenance-service` calls `vehicle-service` to set `TRK-001` to `In Shop`.
   - Vehicle is immediately excluded from Dispatch selection.
   - When maintenance is closed, vehicle returns to `Available`.

---

## 6. Getting Started & Local Setup

### Prerequisites
- **Java:** JDK 21 (Temurin or OpenJDK)
- **Node.js:** v20+ & npm
- **Database:** PostgreSQL running on localhost:5432
- **IAM:** Keycloak v26 running on `http://localhost:8180`

### 1. Database Initialization
Create independent databases in PostgreSQL:
```sql
CREATE DATABASE transitops_vehicle;
CREATE DATABASE transitops_driver;
CREATE DATABASE transitops_trip;
CREATE DATABASE transitops_maintenance;
CREATE DATABASE transitops_expense;
```

### 2. Keycloak Configuration
1. Ensure Keycloak is running at `http://localhost:8180`.
2. Create Realm: `transitops-realm`.
3. Create Public Client: `transitops-frontend-client`
   - Client Authentication: `OFF` (Public)
   - Standard Flow + Direct Access Grants: Enabled
   - Valid Redirect URIs: `http://localhost:5173/*`
   - Web Origins: `http://localhost:5173`
   - PKCE Challenge Method: `S256`
4. Create Realm Roles: `ADMIN`, `FLEET_MANAGER`, `DISPATCHER`, `SAFETY_OFFICER`, `FINANCIAL_ANALYST`.
5. Create Users and assign respective roles.

### 3. Startup Order
Start the services in the following order:

```bash
# 1. Service Registry (Eureka)
cd service-registry && ./mvnw spring-boot:run

# 2. API Gateway
cd api-gateway && ./mvnw spring-boot:run

# 3. Core Microservices (run in separate terminals)
cd vehicle-service && ./mvnw spring-boot:run
cd driver-service && ./mvnw spring-boot:run
cd trip-service && ./mvnw spring-boot:run
cd maintenance-service && ./mvnw spring-boot:run
cd expense-service && ./mvnw spring-boot:run

# 4. Frontend Application
cd frontend && npm install && npm run dev
```

Visit the Eureka Dashboard at `http://localhost:8761` to verify all microservices are registered and healthy. Then launch the frontend at `http://localhost:5173`.

---

## 7. Upcoming Milestones
- [ ] **`report-service`:** Implement data aggregation microservice for Fleet Utilization, Fuel Efficiency, Operational Cost, and ROI.
- [ ] **Event-Driven Architecture (Kafka):** Migrate asynchronous operations (e.g. status changes, trip completion audit events) to Apache Kafka message topics.
- [ ] **Resilience4j:** Implement circuit breakers and fallback mechanisms across all OpenFeign clients.
- [ ] **Distributed Tracing:** Add Micrometer Tracing with Zipkin/Grafana Tempo for distributed request tracking across the Gateway and microservices.
