/**
 * Servicio de usuarios para SOJUS
 */
import apiClient from './client';

const userService = {
  /** Listar todos los usuarios (Admin) */
  getAll: (params = {}) =>
    apiClient.get('/api/users', { params }),

  /** Obtener usuario por ID */
  getById: (id) =>
    apiClient.get(`/api/users/${id}`),

  /** Crear un nuevo usuario (Admin) */
  create: (data) =>
    apiClient.post('/api/users', data),

  /** Actualizar usuario */
  update: (id, data) =>
    apiClient.put(`/api/users/${id}`, data),

  /** Obtener lista de técnicos disponibles */
  getTechnicians: () =>
    apiClient.get('/api/users/technicians'),

  /** Obtener usuarios por rol */
  getByRole: (role) =>
    apiClient.get(`/api/users/role/${role}`),
};

export default userService;
