/**
 * Servicio de autenticación para SOJUS
 */
import apiClient from './client';

const authService = {
  /**
   * Iniciar sesión
   * @param {string} username
   * @param {string} password
   * @returns {Promise} { token, roles, userId, username, fullName }
   */
  login: (username, password) =>
    apiClient.post('/api/auth/login', { username, password }),

  /**
   * Cerrar sesión
   */
  logout: () =>
    apiClient.post('/api/auth/logout'),

  /**
   * Cambiar contraseña
   * @param {string} currentPassword
   * @param {string} newPassword
   */
  changePassword: (currentPassword, newPassword) =>
    apiClient.post('/api/auth/change-password', { currentPassword, newPassword }),

  /**
   * Obtener datos del usuario autenticado
   */
  getMe: () =>
    apiClient.get('/api/auth/me'),
};

export default authService;
