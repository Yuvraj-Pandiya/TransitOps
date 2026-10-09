# TransitOps — Frontend Application

Modern logistics and fleet operations dashboard built with **React 19**, **Vite**, and **Keycloak OpenID Connect**.

## Overview
- **Port:** `5173` (Vite dev server)
- **Framework:** React 19 + Vite
- **Identity & Security:** `keycloak-js` (v26) with PKCE (`S256`)
- **API Communication:** Axios with auto token refresh interceptors targeting the API Gateway (`http://localhost:8765/api`).
- **Styling:** Custom responsive design system with Dark/Light theme toggling (`logistica-theme.css`).

## Key Features
- **Keycloak SSO & PKCE:** Direct browser-level Authorization Code flow with PKCE challenge. All tokens stay strictly in memory and are refreshed automatically before API calls.
- **Dynamic Role-Based UI:**
  - Sidebar navigation links dynamically filter depending on the user's realm roles (`ADMIN`, `FLEET_MANAGER`, `DISPATCHER`, `SAFETY_OFFICER`, `FINANCIAL_ANALYST`).
  - Action buttons (Create Vehicle, Dispatch Trip, Log Maintenance, Add Expense) are conditionally rendered according to user permissions.
- **HTTP Interceptors (`src/api/client.js`):**
  - Transparently calls `keycloak.updateToken(30)` before every request.
  - Automatically redirects to Keycloak login on 401 Unauthorized.
  - Displays toast alert on 403 Forbidden.

## Project Structure
```
frontend/
├── public/              # Static landing page & icons
├── src/
│   ├── api/             # Axios client & Gateway endpoints
│   ├── components/      # Sidebar, Header, Modal, UI components
│   ├── context/         # AuthContext (Keycloak role & session state)
│   ├── pages/           # Dashboard, Vehicles, Drivers, Trips, Maintenance, Fuel, Reports, Settings
│   ├── keycloak.js      # Keycloak client singleton configuration
│   ├── App.jsx          # ProtectedLayout and Route RBAC definitions
│   └── main.jsx         # Application entry point
```

## Running the Frontend
```bash
# Install dependencies
npm install

# Start development server
npm run dev

# Production build
npm run build
```
Once started, access the application at `http://localhost:5173`.
