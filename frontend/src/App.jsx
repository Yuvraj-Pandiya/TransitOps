import { BrowserRouter, Routes, Route, Navigate, useLocation } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import { AuthProvider, useAuth } from './context/AuthContext';
import Sidebar from './components/Sidebar';
import Header from './components/Header';
import Dashboard from './pages/Dashboard';
import Vehicles from './pages/Vehicles';
import Drivers from './pages/Drivers';
import Trips from './pages/Trips';
import Maintenance from './pages/Maintenance';
import Fuel from './pages/Fuel';
import Reports from './pages/Reports';
import Settings from './pages/Settings';

const roleRoutes = {
  ADMIN: ['/dashboard', '/vehicles', '/drivers', '/trips', '/maintenance', '/fuel', '/reports', '/settings'],
  FLEET_MANAGER: ['/dashboard', '/vehicles', '/drivers', '/trips', '/maintenance', '/reports', '/settings'],
  DISPATCHER: ['/dashboard', '/trips', '/settings'],
  SAFETY_OFFICER: ['/drivers', '/settings'],
  FINANCIAL_ANALYST: ['/fuel', '/reports', '/settings'],
};

const defaultLanding = {
  ADMIN: '/dashboard',
  FLEET_MANAGER: '/vehicles',
  DISPATCHER: '/dashboard',
  SAFETY_OFFICER: '/drivers',
  FINANCIAL_ANALYST: '/fuel',
};

const getAllowedRoutes = (userRoles = [], primaryRole) => {
  const upper = userRoles.map((r) => r.toUpperCase());
  if (upper.includes('ADMIN')) {
    return roleRoutes.ADMIN;
  }
  const routes = new Set();
  upper.forEach((role) => {
    if (roleRoutes[role]) {
      roleRoutes[role].forEach((r) => routes.add(r));
    }
  });
  if (routes.size === 0 && primaryRole && roleRoutes[primaryRole.toUpperCase()]) {
    roleRoutes[primaryRole.toUpperCase()].forEach((r) => routes.add(r));
  }
  return routes.size > 0 ? Array.from(routes) : ['/dashboard', '/settings'];
};

const getLandingPath = (userRoles = [], primaryRole) => {
  const upper = userRoles.map((r) => r.toUpperCase());
  if (upper.includes('ADMIN')) return defaultLanding.ADMIN;
  if (upper.includes('FLEET_MANAGER')) return defaultLanding.FLEET_MANAGER;
  if (upper.includes('DISPATCHER')) return defaultLanding.DISPATCHER;
  if (upper.includes('SAFETY_OFFICER')) return defaultLanding.SAFETY_OFFICER;
  if (upper.includes('FINANCIAL_ANALYST')) return defaultLanding.FINANCIAL_ANALYST;
  if (primaryRole && defaultLanding[primaryRole.toUpperCase()]) {
    return defaultLanding[primaryRole.toUpperCase()];
  }
  return '/dashboard';
};

function ProtectedLayout({ children }) {
  const { user } = useAuth();
  const location = useLocation();

  const allowed = getAllowedRoutes(user?.roles, user?.role);
  if (!allowed.includes(location.pathname)) {
    return <Navigate to={getLandingPath(user?.roles, user?.role)} replace />;
  }

  return (
    <div className="app-layout">
      <Sidebar />
      <div className="logistica-main">
        <Header />
        <div className="logistica-content">
          {children}
        </div>
      </div>
    </div>
  );
}

function RootRedirect() {
  const { user } = useAuth();
  return <Navigate to={getLandingPath(user?.roles, user?.role)} replace />;
}

function AppRoutes() {
  const { user } = useAuth();

  return (
    <Routes>
      <Route path="/login" element={<Navigate to={getLandingPath(user?.roles, user?.role)} replace />} />
      <Route path="/dashboard" element={<ProtectedLayout><Dashboard /></ProtectedLayout>} />
      <Route path="/vehicles" element={<ProtectedLayout><Vehicles /></ProtectedLayout>} />
      <Route path="/drivers" element={<ProtectedLayout><Drivers /></ProtectedLayout>} />
      <Route path="/trips" element={<ProtectedLayout><Trips /></ProtectedLayout>} />
      <Route path="/maintenance" element={<ProtectedLayout><Maintenance /></ProtectedLayout>} />
      <Route path="/fuel" element={<ProtectedLayout><Fuel /></ProtectedLayout>} />
      <Route path="/reports" element={<ProtectedLayout><Reports /></ProtectedLayout>} />
      <Route path="/settings" element={<ProtectedLayout><Settings /></ProtectedLayout>} />
      <Route path="/" element={<RootRedirect />} />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <AppRoutes />
        <Toaster
          position="top-right"
          toastOptions={{
            style: {
              background: 'var(--bg-elevated)',
              color: 'var(--text-primary)',
              border: '1px solid var(--border)',
              borderRadius: '10px',
              fontSize: '14px',
            },
            success: { iconTheme: { primary: '#10b981', secondary: '#fff' } },
            error: { iconTheme: { primary: '#ef4444', secondary: '#fff' } },
          }}
        />
      </AuthProvider>
    </BrowserRouter>
  );
}
