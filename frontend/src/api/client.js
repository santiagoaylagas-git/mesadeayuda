/**
 * Cliente HTTP centralizado para SOJUS
 * Axios instance con interceptores JWT usando SecureStore
 */
import axios from 'axios';
import { getToken, clearSession } from '../utils/storage';
import { API_BASE_URL } from '../utils/constants';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: { 'Content-Type': 'application/json' },
});

// Variable para callback de logout (se setea desde el AuthStore)
let onUnauthorized = null;

/**
 * Configura el callback que se ejecuta cuando se recibe un 401
 * @param {Function} callback
 */
export const setUnauthorizedCallback = (callback) => {
  onUnauthorized = callback;
};

// INTERCEPTOR DE REQUEST: adjuntar JWT
apiClient.interceptors.request.use(
  async (config) => {
    const token = await getToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// INTERCEPTOR DE RESPONSE: manejo global de errores
apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response?.status === 401) {
      await clearSession();
      if (onUnauthorized) {
        onUnauthorized();
      }
    }
    return Promise.reject(error);
  }
);

export default apiClient;
