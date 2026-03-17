/**
 * Servicio de tickets para SOJUS
 * CRUD completo + estados, asignación, historial, estadísticas
 */
import apiClient from './client';

const ticketService = {
  /**
   * Listar tickets con paginación y filtros
   * @param {Object} params - { page, size, status, priority, assignedTo, search }
   */
  getAll: (params = {}) =>
    apiClient.get('/api/tickets', { params }),

  /**
   * Obtener detalle de un ticket
   * @param {number} id
   */
  getById: (id) =>
    apiClient.get(`/api/tickets/${id}`),

  /**
   * Crear un nuevo ticket
   * @param {Object} data - { asunto, descripcion, prioridad, canal, juzgadoId, ... }
   */
  create: (data) =>
    apiClient.post('/api/tickets', data),

  /**
   * Actualizar un ticket existente
   * @param {number} id
   * @param {Object} data
   */
  update: (id, data) =>
    apiClient.put(`/api/tickets/${id}`, data),

  /**
   * Cambiar estado de un ticket
   * @param {number} id
   * @param {Object} data - { status, comentario }
   */
  changeStatus: (id, data) =>
    apiClient.patch(`/api/tickets/${id}/status`, data),

  /**
   * Asignar técnico a un ticket
   * @param {number} id
   * @param {Object} data - { tecnicoId }
   */
  assign: (id, data) =>
    apiClient.patch(`/api/tickets/${id}/assign`, data),

  /**
   * Obtener historial de cambios de un ticket
   * @param {number} id
   */
  getHistory: (id) =>
    apiClient.get(`/api/tickets/${id}/history`),

  /**
   * Obtener tickets del usuario autenticado
   */
  getMyTickets: () =>
    apiClient.get('/api/tickets/my'),

  /**
   * Obtener estadísticas del dashboard
   */
  getStats: () =>
    apiClient.get('/api/tickets/stats'),
};

export default ticketService;
