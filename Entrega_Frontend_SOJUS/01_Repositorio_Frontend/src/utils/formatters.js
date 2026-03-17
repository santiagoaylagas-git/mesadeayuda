/**
 * Formateadores de datos para SOJUS
 * Fechas, strings, roles, etc.
 */
import dayjs from 'dayjs';
import 'dayjs/locale/es';
import relativeTime from 'dayjs/plugin/relativeTime';

dayjs.extend(relativeTime);
dayjs.locale('es');

/**
 * Formatea una fecha ISO a formato legible
 * @param {string} dateStr - Fecha ISO
 * @param {string} format - Formato dayjs (default: 'DD/MM/YYYY HH:mm')
 * @returns {string}
 */
export const formatDate = (dateStr, format = 'DD/MM/YYYY HH:mm') => {
  if (!dateStr) return '—';
  const parsed = dayjs(dateStr);
  return parsed.isValid() ? parsed.format(format) : '—';
};

/**
 * Retorna tiempo relativo (hace 2 horas, etc.)
 * @param {string} dateStr
 * @returns {string}
 */
export const timeAgo = (dateStr) => {
  if (!dateStr) return '—';
  const parsed = dayjs(dateStr);
  return parsed.isValid() ? parsed.fromNow() : '—';
};

/**
 * Formatea solo la fecha sin hora
 * @param {string} dateStr
 * @returns {string}
 */
export const formatDateShort = (dateStr) => {
  return formatDate(dateStr, 'DD/MM/YYYY');
};

/**
 * Trunca un string a una longitud máxima con elipsis
 * @param {string} str
 * @param {number} max
 * @returns {string}
 */
export const truncate = (str, max = 50) => {
  if (!str) return '';
  return str.length > max ? str.substring(0, max) + '...' : str;
};

/**
 * Capitaliza la primera letra
 * @param {string} str
 * @returns {string}
 */
export const capitalize = (str) => {
  if (!str) return '';
  return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase();
};

/**
 * Convierte un rol del backend a etiqueta legible
 * @param {string} role
 * @returns {string}
 */
export const formatRole = (role) => {
  const roleLabels = {
    ADMINISTRADOR: 'Administrador',
    OPERADOR: 'Operador',
    TECNICO: 'Técnico',
    GESTOR: 'Gestor',
    AUDITOR: 'Auditor',
  };
  return roleLabels[role] || role || '—';
};

/**
 * Formatea un número de ticket con padding
 * @param {number} id
 * @returns {string}
 */
export const formatTicketId = (id) => {
  if (!id) return '#—';
  return `#${String(id).padStart(4, '0')}`;
};

/**
 * Obtiene las iniciales de un nombre completo
 * @param {string} fullName
 * @returns {string}
 */
export const getInitials = (fullName) => {
  if (!fullName) return '??';
  return fullName
    .split(' ')
    .map((word) => word[0])
    .join('')
    .toUpperCase()
    .substring(0, 2);
};
