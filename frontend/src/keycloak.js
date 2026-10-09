import Keycloak from 'keycloak-js';

// Realm URL: http://localhost:8180/realms/transitops-realm
// Client ID: transitops-frontend-client (public client)
const keycloak = new Keycloak({
  url: 'http://localhost:8180',
  realm: 'transitops-realm',
  clientId: 'transitops-frontend-client',
});

let initPromise = null;

/**
 * Initializes Keycloak with Authorization Code Flow + PKCE (S256)
 * and onLoad: 'login-required'.
 * Requesting 'openid roles profile email' ensures Keycloak includes
 * realm_access.roles and user profile claims in the access token.
 */
export const initKeycloak = () => {
  if (!initPromise) {
    initPromise = keycloak.init({
      onLoad: 'login-required',
      pkceMethod: 'S256',
      checkLoginIframe: false,
      scope: 'openid roles profile email',
    });
  }
  return initPromise;
};

export default keycloak;
