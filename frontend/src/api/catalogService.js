/**
 * Servicio de catálogo de servicios para SOJUS
 */
import apiClient from './client';

const catalogService = {
  /** Obtener lista de servicios disponibles */
  getServices: () =>
    apiClient.get('/api/catalog/services'),

  /** Obtener un servicio por ID */
  getServiceById: (id) =>
    apiClient.get(`/api/catalog/services/${id}`),

  /** Obtener preguntas frecuentes (FAQ) */
  getFAQ: () =>
    apiClient.get('/api/catalog/faq'),

  /** Crear una nueva solicitud de servicio */
  createRequest: (data) =>
    apiClient.post('/api/catalog/requests', data),
};

export default catalogService;
