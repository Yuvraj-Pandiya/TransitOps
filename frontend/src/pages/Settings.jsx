import { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { Shield, User, Settings as SettingsIcon, CheckCircle2, Key, ExternalLink, RefreshCw } from 'lucide-react';
import toast from 'react-hot-toast';

const ROLE_INFO = {
  ADMIN: { label: 'Administrator', desc: 'Full operational access and administrative privileges', color: '#6366f1' },
  FLEET_MANAGER: { label: 'Fleet Manager', desc: 'Manage vehicle registry, maintenance logs, and fleet assets', color: '#06b6d4' },
  DISPATCHER: { label: 'Dispatcher', desc: 'Dispatch routes, track live operations, and complete deliveries', color: '#10b981' },
  SAFETY_OFFICER: { label: 'Safety Officer', desc: 'Driver compliance, safety ratings, and credential auditing', color: '#f59e0b' },
  FINANCIAL_ANALYST: { label: 'Financial Analyst', desc: 'Fuel consumption tracking, operational costs, and ROI reports', color: '#ec4899' },
};

export default function Settings() {
  const { user, keycloak, hasRole } = useAuth();
  const isAdmin = hasRole('ADMIN');

  const [activeTab, setActiveTab] = useState('profile');
  const [tokenRefreshing, setTokenRefreshing] = useState(false);

  // System parameters (operational thresholds in localStorage)
  const [sysConfig, setSysConfig] = useState(() => {
    const saved = localStorage.getItem('transitops_config');
    return saved ? JSON.parse(saved) : {
      licenseWarningDays: '30',
      maxCargoSafetyFactor: '1.0',
      enableRealtimeAlerts: true
    };
  });

  const handleConfigSave = (e) => {
    e.preventDefault();
    localStorage.setItem('transitops_config', JSON.stringify(sysConfig));
    toast.success('System configuration saved!');
  };

  const handleManualTokenRefresh = async () => {
    setTokenRefreshing(true);
    try {
      await keycloak.updateToken(-1); // Force immediate token refresh
      toast.success('Keycloak token refreshed in memory!');
    } catch (err) {
      toast.error('Token refresh failed. Re-authenticating...');
      keycloak.login();
    } finally {
      setTokenRefreshing(false);
    }
  };

  const userRoles = user?.roles || [];
  const recognizedRoles = Object.keys(ROLE_INFO).filter((r) =>
    userRoles.map((ur) => ur.toUpperCase()).includes(r)
  );

  return (
    <div>
      <div className="page-header" style={{ marginBottom: 24 }}>
        <div className="page-header-left">
          <h1 className="page-title"><SettingsIcon size={28} /> Settings & RBAC</h1>
          <p style={{ color: 'var(--text-muted)', fontSize: 16 }}>Manage Keycloak identity, role permissions, and operational thresholds</p>
        </div>
      </div>

      {/* KPI Stats Grid */}
      <div className="kpi-grid">
        <div className="logistica-card" style={{ display: 'flex', alignItems: 'center', gap: 16 }}>
          <div style={{ background: 'rgba(255, 62, 65, 0.1)', padding: 16, borderRadius: 8 }}>
            <User size={28} color="var(--logistica-primary)" />
          </div>
          <div>
            <h4 style={{ margin: 0, fontSize: 15, color: 'var(--text-muted)' }}>Account Identity</h4>
            <p style={{ margin: 0, fontSize: 22, fontWeight: 700 }}>{user?.name || 'Authenticated User'}</p>
          </div>
        </div>
        <div className="logistica-card" style={{ display: 'flex', alignItems: 'center', gap: 16 }}>
          <div style={{ background: 'rgba(81, 207, 237, 0.1)', padding: 16, borderRadius: 8 }}>
            <Shield size={28} color="var(--logistica-secondary)" />
          </div>
          <div>
            <h4 style={{ margin: 0, fontSize: 15, color: 'var(--text-muted)' }}>Keycloak Realm</h4>
            <p style={{ margin: 0, fontSize: 22, fontWeight: 700 }}>transitops-realm</p>
          </div>
        </div>
        <div className="logistica-card" style={{ display: 'flex', alignItems: 'center', gap: 16 }}>
          <div style={{ background: 'rgba(16, 185, 129, 0.1)', padding: 16, borderRadius: 8 }}>
            <CheckCircle2 size={28} color="#10b981" />
          </div>
          <div>
            <h4 style={{ margin: 0, fontSize: 15, color: 'var(--text-muted)' }}>Auth Flow</h4>
            <p style={{ margin: 0, fontSize: 22, fontWeight: 700 }}>PKCE (S256)</p>
          </div>
        </div>
      </div>

      {/* Tabs */}
      <div style={{ display: 'flex', gap: 8, marginBottom: 32, background: 'var(--bg-card)', padding: 8, borderRadius: 8, width: 'fit-content', border: '1px solid var(--border-color)', boxShadow: 'var(--card-shadow)' }}>
        <button id="tab-profile"
          style={{
            display: 'flex', alignItems: 'center', gap: 8, padding: '12px 24px',
            borderRadius: 6, border: 'none', cursor: 'pointer', fontSize: 16, fontWeight: 600,
            background: activeTab === 'profile' ? 'var(--logistica-primary)' : 'transparent',
            color: activeTab === 'profile' ? 'white' : 'var(--text-muted)',
            transition: 'all 0.3s', fontFamily: 'inherit'
          }}
          onClick={() => setActiveTab('profile')}
        >
          <User size={18} /> Profile & Roles
        </button>
        <button id="tab-system"
          style={{
            display: 'flex', alignItems: 'center', gap: 8, padding: '12px 24px',
            borderRadius: 6, border: 'none', cursor: 'pointer', fontSize: 16, fontWeight: 600,
            background: activeTab === 'system' ? 'var(--logistica-primary)' : 'transparent',
            color: activeTab === 'system' ? 'white' : 'var(--text-muted)',
            transition: 'all 0.3s', fontFamily: 'inherit'
          }}
          onClick={() => setActiveTab('system')}
        >
          <SettingsIcon size={18} /> System Config
        </button>
      </div>

      <div style={{ display: 'flex', flexDirection: 'column', gap: 24 }}>
        {/* Tab 1: Profile & Keycloak RBAC */}
        {activeTab === 'profile' && (
          <div className="form-grid" style={{ gridTemplateColumns: '1.4fr 1fr', alignItems: 'start', gap: 32 }}>
            {/* Keycloak User Profile */}
            <div className="logistica-card">
              <div className="card-header" style={{ padding: 0, marginBottom: 20 }}>
                <span className="card-title" style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                  <User size={18} color="var(--primary-light)" /> Keycloak Identity Details
                </span>
              </div>

              <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
                <div className="form-group">
                  <label className="form-label" style={{ fontSize: 12, textTransform: 'uppercase', color: 'var(--text-muted)' }}>Display Name</label>
                  <input className="logistica-input" value={user?.name || ''} readOnly disabled style={{ opacity: 0.9 }} />
                </div>

                <div className="form-group">
                  <label className="form-label" style={{ fontSize: 12, textTransform: 'uppercase', color: 'var(--text-muted)' }}>Email Address</label>
                  <input className="logistica-input" value={user?.email || 'N/A'} readOnly disabled style={{ opacity: 0.9 }} />
                </div>

                <div className="form-group">
                  <label className="form-label" style={{ fontSize: 12, textTransform: 'uppercase', color: 'var(--text-muted)' }}>Username</label>
                  <input className="logistica-input" value={user?.username || ''} readOnly disabled style={{ opacity: 0.9 }} />
                </div>

                <div className="form-group">
                  <label className="form-label" style={{ fontSize: 12, textTransform: 'uppercase', color: 'var(--text-muted)' }}>Subject Identifier (UUID)</label>
                  <input className="logistica-input" value={user?.id || ''} readOnly disabled style={{ fontSize: 12, opacity: 0.8 }} />
                </div>

                <div style={{ marginTop: 8, display: 'flex', gap: 12 }}>
                  <button
                    type="button"
                    className="btn-logistica"
                    onClick={() => keycloak.accountManagement()}
                    style={{ display: 'inline-flex', alignItems: 'center', gap: 8 }}
                  >
                    <ExternalLink size={16} /> Manage Keycloak Account
                  </button>
                  {isAdmin && (
                    <a
                      href="http://localhost:8180/admin/transitops-realm/console/#/transitops-realm/users"
                      target="_blank"
                      rel="noreferrer"
                      className="btn-logistica-secondary"
                      style={{ display: 'inline-flex', alignItems: 'center', gap: 8, textDecoration: 'none' }}
                    >
                      <Shield size={16} /> Keycloak Admin Console
                    </a>
                  )}
                </div>
              </div>
            </div>

            {/* Realm Roles and Security Info */}
            <div style={{ display: 'flex', flexDirection: 'column', gap: 24 }}>
              <div className="logistica-card">
                <div className="card-header" style={{ padding: 0, marginBottom: 16 }}>
                  <span className="card-title" style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                    <Shield size={18} color="var(--logistica-primary)" /> Assigned Realm Roles
                  </span>
                </div>
                <p style={{ fontSize: 13, color: 'var(--text-muted)', marginBottom: 16 }}>
                  Roles parsed from <code style={{ color: 'var(--primary-light)' }}>tokenParsed.realm_access.roles</code>:
                </p>

                <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
                  {recognizedRoles.length > 0 ? (
                    recognizedRoles.map((roleKey) => {
                      const meta = ROLE_INFO[roleKey];
                      return (
                        <div
                          key={roleKey}
                          style={{
                            padding: 12,
                            borderRadius: 8,
                            background: `${meta.color}15`,
                            border: `1px solid ${meta.color}35`,
                          }}
                        >
                          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 4 }}>
                            <span style={{ fontWeight: 700, color: meta.color, fontSize: 14 }}>{meta.label}</span>
                            <span className="logistica-badge badge-success" style={{ fontSize: 11 }}>ACTIVE</span>
                          </div>
                          <div style={{ fontSize: 12, color: 'var(--text-muted)' }}>{meta.desc}</div>
                        </div>
                      );
                    })
                  ) : (
                    <div style={{
                      padding: '12px',
                      background: 'rgba(239, 68, 68, 0.08)',
                      borderRadius: 8,
                      border: '1px solid rgba(239, 68, 68, 0.2)'
                    }}>
                      <div style={{ color: '#ef4444', fontWeight: 600, fontSize: 13, marginBottom: 4 }}>
                        No recognized roles in active token.
                      </div>
                      <div style={{ color: 'var(--text-muted)', fontSize: 12, lineHeight: 1.5, marginBottom: 8 }}>
                        Token currently holds: <code>{JSON.stringify(user?.roles || [])}</code>
                      </div>
                      <div style={{ color: '#94a3b8', fontSize: 12 }}>
                        If you just assigned roles in Keycloak Admin, click <strong>"Test Manual Token Refresh"</strong> below or Log Out and Log In to fetch the new token!
                      </div>
                    </div>
                  )}
                </div>
              </div>

              {/* In-Memory Token & Session Details */}
              <div className="logistica-card">
                <div className="card-header" style={{ padding: 0, marginBottom: 14 }}>
                  <span className="card-title" style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                    <Key size={18} color="var(--primary-light)" /> Session & In-Memory Token
                  </span>
                </div>
                <div style={{ fontSize: 13, color: 'var(--text-muted)', lineHeight: 1.6, marginBottom: 14 }}>
                  <div><strong>Storage:</strong> Strictly in-memory (No tokens in localStorage)</div>
                  <div><strong>Auto-refresh:</strong> Refreshes automatically before requests via Axios interceptor</div>
                  <div><strong>Token Expired:</strong> {keycloak.isTokenExpired() ? 'Yes' : 'No'}</div>
                </div>

                <button
                  type="button"
                  onClick={handleManualTokenRefresh}
                  className="btn-logistica-secondary"
                  disabled={tokenRefreshing}
                  style={{ display: 'inline-flex', alignItems: 'center', gap: 6, fontSize: 13 }}
                >
                  <RefreshCw size={14} className={tokenRefreshing ? 'spin-icon' : ''} />
                  {tokenRefreshing ? 'Refreshing...' : 'Test Manual Token Refresh'}
                </button>
              </div>
            </div>
          </div>
        )}

        {/* Tab 2: System Config */}
        {activeTab === 'system' && (
          <div className="logistica-card" style={{ maxWidth: 640 }}>
            <div className="card-header" style={{ padding: 0, marginBottom: 20 }}>
              <span className="card-title" style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                <SettingsIcon size={18} color="var(--primary-light)" /> Operation Threshold Settings
              </span>
            </div>
            <form onSubmit={handleConfigSave} style={{ display: 'flex', flexDirection: 'column', gap: 20 }}>
              <div className="form-group">
                <label className="form-label">License Expiry Notification Threshold (Days)</label>
                <input
                  type="number"
                  className="logistica-input"
                  value={sysConfig.licenseWarningDays}
                  onChange={e => setSysConfig(c => ({ ...c, licenseWarningDays: e.target.value }))}
                  min="5"
                  max="180"
                />
                <span style={{ fontSize: 12, color: 'var(--text-muted)' }}>Displays warnings on the Dashboard if license expiration is within this range.</span>
              </div>

              <div className="form-group">
                <label className="form-label">Max Load Safety Buffer Factor</label>
                <select
                  className="logistica-input"
                  value={sysConfig.maxCargoSafetyFactor}
                  onChange={e => setSysConfig(c => ({ ...c, maxCargoSafetyFactor: e.target.value }))}
                >
                  <option value="1.0">1.0x (Normal load limit capacity)</option>
                  <option value="0.95">0.95x (Enforce 5% safety margin)</option>
                  <option value="0.90">0.90x (Enforce 10% safety margin)</option>
                </select>
                <span style={{ fontSize: 12, color: 'var(--text-muted)' }}>Limits the cargo load assignment based on safety margin settings.</span>
              </div>

              <div className="form-group" style={{ flexDirection: 'row', alignItems: 'center', gap: 10, marginTop: 8 }}>
                <input
                  type="checkbox"
                  id="realtime_alert"
                  style={{ width: 18, height: 18, cursor: 'pointer' }}
                  checked={sysConfig.enableRealtimeAlerts}
                  onChange={e => setSysConfig(c => ({ ...c, enableRealtimeAlerts: e.target.checked }))}
                />
                <label htmlFor="realtime_alert" style={{ fontSize: 14, fontWeight: 500, cursor: 'pointer' }}>Enable System-wide Real-time KPI Refreshing</label>
              </div>

              <button type="submit" className="btn-logistica" style={{ width: 'fit-content', marginTop: 10 }}>
                Save System Config
              </button>
            </form>
          </div>
        )}
      </div>
    </div>
  );
}
