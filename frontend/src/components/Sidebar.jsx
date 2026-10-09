import { NavLink } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import {
  LayoutDashboard, Truck, Users, Route, Wrench,
  Fuel, BarChart3, LogOut, Zap, Settings
} from 'lucide-react';

const navItems = [
  { to: '/dashboard', icon: LayoutDashboard, label: 'Dashboard', section: 'overview', roles: ['ADMIN', 'FLEET_MANAGER', 'DISPATCHER'] },
  { to: '/vehicles', icon: Truck, label: 'Vehicles', section: 'fleet', roles: ['ADMIN', 'FLEET_MANAGER'] },
  { to: '/drivers', icon: Users, label: 'Drivers', section: 'fleet', roles: ['ADMIN', 'FLEET_MANAGER', 'SAFETY_OFFICER'] },
  { to: '/trips', icon: Route, label: 'Trip Management', section: 'operations', roles: ['ADMIN', 'FLEET_MANAGER', 'DISPATCHER'] },
  { to: '/maintenance', icon: Wrench, label: 'Maintenance', section: 'operations', roles: ['ADMIN', 'FLEET_MANAGER'] },
  { to: '/fuel', icon: Fuel, label: 'Fuel & Expenses', section: 'finance', roles: ['ADMIN', 'FINANCIAL_ANALYST'] },
  { to: '/reports', icon: BarChart3, label: 'Reports & Analytics', section: 'finance', roles: ['ADMIN', 'FLEET_MANAGER', 'FINANCIAL_ANALYST'] },
  { to: '/settings', icon: Settings, label: 'Settings', section: 'overview', roles: ['ADMIN', 'FLEET_MANAGER', 'DISPATCHER', 'SAFETY_OFFICER', 'FINANCIAL_ANALYST'] },
];

const sections = {
  overview: 'Overview',
  fleet: 'Fleet Management',
  operations: 'Operations',
  finance: 'Finance',
};

const roleLabels = {
  ADMIN: 'Administrator',
  FLEET_MANAGER: 'Fleet Manager',
  DISPATCHER: 'Dispatcher',
  SAFETY_OFFICER: 'Safety Officer',
  FINANCIAL_ANALYST: 'Financial Analyst',
};

export default function Sidebar() {
  const { user, logout, hasRole } = useAuth();

  const handleLogout = () => {
    logout();
  };

  // Filter nav items based on the user's Keycloak roles
  const filteredNavItems = navItems.filter((item) => item.roles.some((r) => hasRole(r)));

  const grouped = filteredNavItems.reduce((acc, item) => {
    if (!acc[item.section]) acc[item.section] = [];
    acc[item.section].push(item);
    return acc;
  }, {});

  const initials = user?.name?.split(' ').map((n) => n[0]).join('').toUpperCase().slice(0, 2) || 'TO';
  const displayRole = roleLabels[user?.role] || user?.role?.replace('_', ' ') || 'User';

  return (
    <aside className="logistica-sidebar">
      <div className="logistica-sidebar-logo">
        <Zap size={28} color="var(--logistica-primary)" strokeWidth={2.5} />
        <h2>TransitOps</h2>
      </div>

      <nav className="logistica-nav">
        {Object.entries(grouped).map(([section, items]) => (
          <div key={section} className="mb-3">
            <div className="text-muted" style={{ padding: '0 24px', fontSize: '12px', textTransform: 'uppercase', fontWeight: 600, letterSpacing: '1px', marginBottom: '8px' }}>
              {sections[section]}
            </div>
            {items.map(({ to, icon: Icon, label }) => (
              <NavLink
                key={to}
                to={to}
                className={({ isActive }) => `logistica-nav-item${isActive ? ' active' : ''}`}
              >
                <Icon size={18} />
                {label}
              </NavLink>
            ))}
          </div>
        ))}
      </nav>

      <div className="logistica-sidebar-footer">
        <div className="d-flex align-items-center justify-content-between">
          <div className="d-flex align-items-center gap-2">
            <div style={{ width: 36, height: 36, borderRadius: '50%', backgroundColor: 'var(--logistica-secondary)', color: 'white', display: 'flex', alignItems: 'center', justifyContent: 'center', fontWeight: 'bold' }}>
              {initials}
            </div>
            <div>
              <div style={{ fontWeight: 600, fontSize: '14px', color: 'var(--text-main)' }}>{user?.name}</div>
              <div style={{ fontSize: '12px', color: 'var(--text-muted)' }}>{displayRole}</div>
            </div>
          </div>
          <button
            onClick={handleLogout}
            className="btn-icon"
            title="Logout"
          >
            <LogOut size={16} />
          </button>
        </div>
      </div>
    </aside>
  );
}
