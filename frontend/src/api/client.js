import axios from 'axios';
import toast from 'react-hot-toast';
import keycloak from '../keycloak';

const api = axios.create({
  baseURL: 'http://localhost:8765/api',
  timeout: 10000,
  headers: { 'Content-Type': 'application/json' },
});

// Request interceptor - take token from keycloak.token in memory & refresh if expiring in 30s
api.interceptors.request.use(
  async (config) => {
    if (keycloak && keycloak.authenticated) {
      try {
        await keycloak.updateToken(30);
      } catch (err) {
        console.warn('Failed to refresh Keycloak token, redirecting to login', err);
        keycloak.login();
        return Promise.reject(err);
      }

      if (keycloak.token) {
        config.headers.Authorization = `Bearer ${keycloak.token}`;
      }
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor - handle 401 (redirect to login) and 403 (forbidden message)
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      keycloak.login();
    } else if (error.response?.status === 403) {
      toast.error('You are not allowed to do this');
    }
    return Promise.reject(error);
  }
);

export default api;
