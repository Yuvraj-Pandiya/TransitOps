# TransitOps — API Gateway

The single entry point and security reverse proxy for the TransitOps ecosystem built on **Spring Cloud Gateway (WebFlux)**.

## Overview
- **Port:** `8765`
- **Type:** Reactive Non-blocking Gateway (Netty)
- **Role:** Central Routing, CORS handling, and OAuth2/JWT Resource Server with Keycloak.

## Security Architecture
- Validates Keycloak JWT tokens emitted by `transitops-realm` via `jwk-set-uri`:
  `http://localhost:8180/realms/transitops-realm/protocol/openid-connect/certs`
- Extracts roles from `jwt.getClaim("realm_access").roles` and maps them to Spring authorities (`ROLE_<ROLE_NAME>`).
- Enforces Role-Based Access Control (RBAC) on incoming routes before dispatching to downstream microservices.

## Routing Rules
| Inbound Path | StripPrefix | Target (Eureka Discovery) | Permitted Roles |
|---|---|---|---|
| `/api/vehicles/**` | `1` | `lb://vehicle-service` | GET: All roles, Write: `ADMIN`, `FLEET_MANAGER` |
| `/api/drivers/**` | `1` | `lb://driver-service` | GET: All roles, Write: `ADMIN`, `SAFETY_OFFICER` |
| `/api/trips/**` | `1` | `lb://trip-service` | GET: All roles, Write: `ADMIN`, `DISPATCHER` |
| `/api/maintenances/**` | `1` | `lb://maintenance-service` | GET: `ADMIN`, `MANAGER`, `FINANCE`, Write: `ADMIN`, `FLEET_MANAGER` |
| `/api/expenses/**` | `1` | `lb://expense-service` | GET: `ADMIN`, `MANAGER`, `FINANCE`, Write: `ADMIN`, `FINANCIAL_ANALYST` |
| `/api/reports/**` | `1` | `lb://report-service` | GET: `ADMIN`, `FLEET_MANAGER`, `FINANCIAL_ANALYST` |

## Running the Service
```bash
./mvnw spring-boot:run
```
