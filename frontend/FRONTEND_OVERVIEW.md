# Frontend Overview

## Technology Stack
- **Framework:** React 19 (via Vite)
- **Identity & Authentication:** Keycloak JS (`keycloak-js`) with PKCE (S256)
- **State Management:** React Context (`AuthContext` reading Keycloak token & parsed realm roles)
- **HTTP Client:** Axios (`src/api/client.js` with auto token refresh and 401/403 interceptors)
- **Styling:** Custom CSS (`App.css`, `index.css`, `logistica-theme.css`) and Lucide React icons
- **Charts/PDFs:** Recharts, jsPDF, PapaParse

## Architecture & API Rewire
The monolithic API base URL (`http://localhost:5000/api`) has been updated to point to the **API Gateway** (`http://localhost:8765/api`).
All outgoing API calls pass through the Gateway, which in turn routes requests to the separated microservices.

### Endpoint Mapping Table

| Frontend Page | API Call / Route       | API Gateway Route      | Downstream Microservice | Required Roles (RBAC) |
|---------------|------------------------|------------------------|-------------------------|-----------------------|
| Vehicles      | `/vehicles/**`         | `/api/vehicles/**`     | `vehicle-service`       | GET: ALL, Write: ADMIN, FLEET_MANAGER |
| Drivers       | `/drivers/**`          | `/api/drivers/**`      | `driver-service`        | GET: ALL, Write: ADMIN, SAFETY_OFFICER |
| Trips         | `/trips/**`            | `/api/trips/**`        | `trip-service`          | GET: ALL, Write: ADMIN, DISPATCHER |
| Maintenance   | `/maintenances/**`     | `/api/maintenances/**` | `maintenance-service`   | GET: ADMIN, MANAGER, FINANCE, Write: ADMIN, FLEET_MANAGER |
| Fuel/Expenses | `/expenses/**`         | `/api/expenses/**`     | `expense-service`       | GET: ADMIN, MANAGER, FINANCE, Write: ADMIN, FINANCIAL_ANALYST |
| Reports       | `/reports/**`          | `/api/reports/**`      | `report-service`        | GET: ADMIN, MANAGER, FINANCE |

## Keycloak Integration
- **Realm URL:** `http://localhost:8180/realms/transitops-realm`
- **Client ID:** `transitops-frontend-client` (public client)
- **Auth Flow:** Authorization Code with PKCE (`pkceMethod: 'S256'`), `onLoad: 'login-required'`
- **Token Storage:** Tokens are maintained strictly in memory on the `keycloak` instance, never stored in `localStorage`.
- **Axios Interceptor:** Takes token from `keycloak.token` and calls `keycloak.updateToken(30)` before each request for transparent token refresh.
- **Error Handling:** 401 automatically redirects to Keycloak login; 403 triggers a `"You are not allowed to do this"` toast notification.
- **RBAC:** Roles are parsed directly from `keycloak.tokenParsed.realm_access.roles` (`ADMIN`, `FLEET_MANAGER`, `DISPATCHER`, `SAFETY_OFFICER`, `FINANCIAL_ANALYST`).
- **UI Enforcement:** Sidebar menu navigation items and action buttons (Create/Edit/Delete/Dispatch/Complete) are dynamically hidden based on role permissions.
- **Logout:** Explicitly invokes `keycloak.logout()` to terminate the Keycloak SSO session.
