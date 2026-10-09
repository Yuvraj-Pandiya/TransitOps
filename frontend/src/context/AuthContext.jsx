import { createContext, useContext, useState, useEffect, useCallback } from 'react';
import keycloak, { initKeycloak } from '../keycloak';

const AuthContext = createContext(null);

const KNOWN_ROLES = ['ADMIN', 'FLEET_MANAGER', 'DISPATCHER', 'SAFETY_OFFICER', 'FINANCIAL_ANALYST'];

const normalizeRole = (role) => {
  if (!role) return '';
  const upper = role.toUpperCase();
  if (upper === 'ADMIN' || upper === 'ADMINISTRATOR') return 'ADMIN';
  if (upper === 'FLEET_MANAGER' || upper === 'FLEET' || upper === 'MANAGER') return 'FLEET_MANAGER';
  if (upper === 'DISPATCHER' || upper === 'DISPATCH') return 'DISPATCHER';
  if (upper === 'SAFETY_OFFICER' || upper === 'SAFETY') return 'SAFETY_OFFICER';
  if (upper === 'FINANCIAL_ANALYST' || upper === 'FINANCE' || upper === 'ANALYST') return 'FINANCIAL_ANALYST';
  return upper;
};

export const AuthProvider = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let isMounted = true;

    initKeycloak()
      .then((authenticated) => {
        if (!isMounted) return;

        setIsAuthenticated(!!authenticated);

        if (authenticated && keycloak.tokenParsed) {
          const parsed = keycloak.tokenParsed;
          const realmRoles = parsed.realm_access?.roles || [];
          const clientRoles = parsed.resource_access?.['transitops-frontend-client']?.roles || [];
          const allRoles = Array.from(new Set([...realmRoles, ...clientRoles]));
          const normalizedRoles = allRoles.map(normalizeRole);

          // Log token parsed info to assist local testing & debugging
          console.log('[TransitOps] Keycloak Token Authenticated:', {
            username: parsed.preferred_username,
            name: parsed.name,
            email: parsed.email,
            realmRoles,
            clientRoles,
            normalizedRoles,
          });

          // Determine primary display role
          const primaryRole = KNOWN_ROLES.find((r) => normalizedRoles.includes(r)) || normalizedRoles[0] || 'USER';
          const displayName = parsed.name || (parsed.given_name ? `${parsed.given_name} ${parsed.family_name || ''}`.trim() : '') || parsed.preferred_username || 'User';

          setUser({
            id: parsed.sub,
            username: parsed.preferred_username || '',
            name: displayName,
            email: parsed.email || '',
            role: primaryRole,
            roles: normalizedRoles,
          });
        }
        setLoading(false);
      })
      .catch((err) => {
        console.error('Keycloak initialization failed:', err);
        if (isMounted) {
          setLoading(false);
        }
      });

    return () => {
      isMounted = false;
    };
  }, []);

  // Logout calls Keycloak logout to end SSO session
  const logout = useCallback(() => {
    keycloak.logout({ redirectUri: window.location.origin });
  }, []);

  const login = useCallback(() => {
    keycloak.login();
  }, []);

  // Check roles against realm_access.roles and client roles (normalized)
  const hasRole = useCallback((...requiredRoles) => {
    if (!keycloak.tokenParsed) return false;
    const realmRoles = keycloak.tokenParsed.realm_access?.roles || [];
    const clientRoles = keycloak.tokenParsed.resource_access?.['transitops-frontend-client']?.roles || [];
    const allRoles = [...realmRoles, ...clientRoles].map(normalizeRole);

    // ADMIN has universal operational role access
    if (allRoles.includes('ADMIN')) {
      return true;
    }

    return requiredRoles.some((role) => {
      const target = normalizeRole(role);
      return allRoles.includes(target);
    });
  }, []);

  if (loading) {
    return (
      <div style={{
        minHeight: '100vh',
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        background: '#0a0d14',
        color: '#f8fafc',
        fontFamily: 'Inter, system-ui, sans-serif',
      }}>
        <div style={{
          width: 48,
          height: 48,
          borderRadius: '50%',
          border: '3px solid rgba(99, 102, 241, 0.2)',
          borderTopColor: '#6366f1',
          animation: 'spin 0.8s linear infinite',
          marginBottom: 16,
        }} />
        <div style={{ fontSize: 16, fontWeight: 600 }}>Authenticating with Keycloak...</div>
        <div style={{ fontSize: 13, color: '#94a3b8', marginTop: 4 }}>Securing your session with PKCE</div>
        <style>{`
          @keyframes spin {
            to { transform: rotate(360deg); }
          }
        `}</style>
      </div>
    );
  }

  return (
    <AuthContext.Provider
      value={{
        keycloak,
        user,
        roles: user?.roles || [],
        isAuthenticated,
        loading,
        hasRole,
        login,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used inside AuthProvider');
  return ctx;
};
