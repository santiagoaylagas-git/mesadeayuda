/**
 * Constantes globales del sistema SOJUS
 * Estados de tickets, prioridades, roles y permisos
 */

// URL base de la API
export const API_BASE_URL = process.env.EXPO_PUBLIC_API_URL || 'http://localhost:8080';

// Estados de tickets
export const TICKET_STATUS = {
  SOLICITADO: { key: 'SOLICITADO', label: 'Solicitado', color: 'open' },
  ASIGNADO: { key: 'ASIGNADO', label: 'Asignado', color: 'inProgress' },
  EN_CURSO: { key: 'EN_CURSO', label: 'En Curso', color: 'inProgress' },
  PENDIENTE: { key: 'PENDIENTE', label: 'Pendiente', color: 'pending' },
  RESUELTO: { key: 'RESUELTO', label: 'Resuelto', color: 'resolved' },
  CERRADO: { key: 'CERRADO', label: 'Cerrado', color: 'closed' },
};

// Transiciones de estado válidas
export const STATUS_TRANSITIONS = {
  SOLICITADO: ['ASIGNADO', 'CERRADO'],
  ASIGNADO: ['EN_CURSO', 'CERRADO'],
  EN_CURSO: ['RESUELTO', 'PENDIENTE', 'CERRADO'],
  PENDIENTE: ['EN_CURSO', 'CERRADO'],
  RESUELTO: ['CERRADO'],
  CERRADO: [],
};

// Prioridades
export const PRIORITIES = {
  BAJA: { key: 'BAJA', label: 'Baja', color: 'low' },
  MEDIA: { key: 'MEDIA', label: 'Media', color: 'medium' },
  ALTA: { key: 'ALTA', label: 'Alta', color: 'high' },
  CRITICA: { key: 'CRITICA', label: 'Crítica', color: 'critical' },
};

// Roles del sistema
export const ROLES = {
  ADMIN: 'ADMINISTRADOR',
  OPERADOR: 'OPERADOR',
  TECNICO: 'TECNICO',
  GESTOR: 'GESTOR',
  AUDITOR: 'AUDITOR',
};

// Permisos por rol (RBAC)
export const PERMISSIONS = {
  ADMINISTRADOR: [
    'tickets.create', 'tickets.read', 'tickets.update', 'tickets.delete',
    'tickets.assign', 'users.manage', 'reports.view', 'catalog.manage',
    'inventory.manage', 'territorial.manage',
  ],
  OPERADOR: [
    'tickets.create', 'tickets.read', 'tickets.update',
    'catalog.read', 'inventory.read',
  ],
  TECNICO: [
    'tickets.read', 'tickets.update.assigned', 'tickets.resolve',
    'inventory.read', 'inventory.update',
  ],
  GESTOR: [
    'tickets.read', 'reports.view', 'catalog.read',
  ],
  AUDITOR: [
    'tickets.read', 'reports.view', 'inventory.read',
  ],
};

// Canales de entrada de tickets
export const CHANNELS = {
  APP_MOVIL: 'App Móvil',
  WEB: 'Web',
  EMAIL: 'Email',
  TELEFONO: 'Teléfono',
  PRESENCIAL: 'Presencial',
};

// Claves de almacenamiento seguro
export const STORAGE_KEYS = {
  JWT_TOKEN: 'jwt_token',
  USER_DATA: 'user_data',
  REFRESH_TOKEN: 'refresh_token',
};
