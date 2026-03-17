/**
 * Manejador global de errores HTTP para SOJUS
 * Traduce códigos de error a mensajes comprensibles en español
 */

const ERROR_MESSAGES = {
  400: 'Datos inválidos. Revisá los campos del formulario.',
  401: 'Tu sesión expiró. Por favor iniciá sesión nuevamente.',
  403: 'No tenés permisos para realizar esta acción.',
  404: 'El recurso solicitado no fue encontrado.',
  409: 'Ya existe un registro con esos datos.',
  422: 'Error de validación.',
  500: 'Error interno del servidor. Intentá más tarde.',
  502: 'El servidor no está disponible. Intentá más tarde.',
  503: 'Servicio temporalmente no disponible. Intentá más tarde.',
};

/**
 * Extrae un mensaje de error legible de un error de Axios
 * @param {Error} error - Error de Axios
 * @returns {string} Mensaje de error en español
 */
export const getErrorMessage = (error) => {
  // Sin conexión a internet
  if (!error.response && error.message === 'Network Error') {
    return 'Sin conexión. Verificá tu red e intentá nuevamente.';
  }

  // Timeout
  if (error.code === 'ECONNABORTED') {
    return 'La solicitud tardó demasiado. Intentá nuevamente.';
  }

  // Error con respuesta del servidor
  if (error.response) {
    const { status, data } = error.response;

    // Si el servidor envió un mensaje específico, usarlo
    if (data?.message && status !== 401) {
      // Para 422, agregar el detalle del servidor
      if (status === 422) {
        return `Error de validación: ${data.message}`;
      }
      return data.message;
    }

    // Mensaje genérico por código de estado
    return ERROR_MESSAGES[status] || `Error inesperado (código ${status}). Intentá nuevamente.`;
  }

  // Error genérico de red u otro
  return 'Ocurrió un error inesperado. Intentá nuevamente.';
};

/**
 * Verifica si un error es de autenticación (401)
 * @param {Error} error
 * @returns {boolean}
 */
export const isAuthError = (error) => {
  return error.response?.status === 401;
};

/**
 * Verifica si un error es de permisos (403)
 * @param {Error} error
 * @returns {boolean}
 */
export const isForbiddenError = (error) => {
  return error.response?.status === 403;
};

/**
 * Verifica si un error es de red (sin conexión)
 * @param {Error} error
 * @returns {boolean}
 */
export const isNetworkError = (error) => {
  return !error.response && error.message === 'Network Error';
};
