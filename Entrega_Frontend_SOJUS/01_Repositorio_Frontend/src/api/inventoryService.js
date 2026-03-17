/**
 * Servicio de inventario para SOJUS
 */
import apiClient from './client';

const inventoryService = {
  /** Obtener lista de activos (hardware + software) */
  getAssets: (params = {}) =>
    apiClient.get('/api/inventory/assets', { params }),

  /** Obtener detalle de un activo */
  getAssetById: (id) =>
    apiClient.get(`/api/inventory/assets/${id}`),

  /** Obtener lista de hardware */
  getHardware: (params = {}) =>
    apiClient.get('/api/inventory/hardware', { params }),

  /** Obtener detalle de hardware */
  getHardwareById: (id) =>
    apiClient.get(`/api/inventory/hardware/${id}`),

  /** Obtener lista de software */
  getSoftware: (params = {}) =>
    apiClient.get('/api/inventory/software', { params }),

  /** Obtener detalle de software */
  getSoftwareById: (id) =>
    apiClient.get(`/api/inventory/software/${id}`),
};

export default inventoryService;
