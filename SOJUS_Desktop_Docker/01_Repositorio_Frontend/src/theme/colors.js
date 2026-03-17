/**
 * Paleta de colores institucional SOJUS
 * Sistema de Mesa de Ayuda — Poder Judicial Provincia de Santa Fe
 */
export const colors = {
  // Primarios — Azul institucional judicial
  primary: {
    50: '#EFF6FF',
    100: '#DBEAFE',
    500: '#1E40AF',
    600: '#1D4ED8',
    700: '#1E3A8A',
    900: '#0F172A',
  },

  // Semánticos para estados de tickets
  status: {
    open: { bg: '#FEF3C7', text: '#92400E', border: '#FCD34D' },
    inProgress: { bg: '#DBEAFE', text: '#1E40AF', border: '#93C5FD' },
    pending: { bg: '#F3F4F6', text: '#374151', border: '#D1D5DB' },
    resolved: { bg: '#D1FAE5', text: '#065F46', border: '#6EE7B7' },
    closed: { bg: '#F3F4F6', text: '#6B7280', border: '#E5E7EB' },
    critical: { bg: '#FEE2E2', text: '#991B1B', border: '#FCA5A5' },
  },

  // Prioridades
  priority: {
    low: '#10B981',
    medium: '#F59E0B',
    high: '#EF4444',
    critical: '#7C3AED',
  },

  // Funcionales
  success: '#059669',
  successBg: 'rgba(5,150,105,0.08)',
  warning: '#D97706',
  warningBg: 'rgba(217,119,6,0.08)',
  danger: '#DC2626',
  dangerBg: 'rgba(220,38,38,0.08)',
  info: '#2563EB',
  infoBg: 'rgba(37,99,235,0.08)',

  // Neutrales
  surface: '#FFFFFF',
  background: '#F8FAFC',
  border: '#E2E8F0',
  textPrimary: '#0F172A',
  textSecondary: '#64748B',
  textDisabled: '#CBD5E1',
  white: '#FFFFFF',
  black: '#000000',

  // Compatibilidad con el tema anterior
  accent: '#0369A1',
  accentLight: '#0284C7',
};
