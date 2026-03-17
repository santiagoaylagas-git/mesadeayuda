/**
 * Validadores de formularios para SOJUS
 * Retornan null si es válido o un string con el mensaje de error
 */

/**
 * Valida que un campo no esté vacío
 * @param {string} value
 * @param {string} fieldName - Nombre del campo para el mensaje
 * @returns {string|null}
 */
export const required = (value, fieldName = 'Este campo') => {
  if (!value || (typeof value === 'string' && !value.trim())) {
    return `${fieldName} es obligatorio`;
  }
  return null;
};

/**
 * Valida formato de email
 * @param {string} value
 * @returns {string|null}
 */
export const email = (value) => {
  if (!value) return null; // No valida vacío, usar required para eso
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(value)) {
    return 'Ingresá un email válido';
  }
  return null;
};

/**
 * Valida longitud mínima
 * @param {string} value
 * @param {number} min
 * @param {string} fieldName
 * @returns {string|null}
 */
export const minLength = (value, min, fieldName = 'Este campo') => {
  if (!value) return null;
  if (value.length < min) {
    return `${fieldName} debe tener al menos ${min} caracteres`;
  }
  return null;
};

/**
 * Valida longitud máxima
 * @param {string} value
 * @param {number} max
 * @param {string} fieldName
 * @returns {string|null}
 */
export const maxLength = (value, max, fieldName = 'Este campo') => {
  if (!value) return null;
  if (value.length > max) {
    return `${fieldName} no puede exceder ${max} caracteres`;
  }
  return null;
};

/**
 * Valida contraseña segura
 * @param {string} value
 * @returns {string|null}
 */
export const password = (value) => {
  if (!value) return null;
  if (value.length < 6) {
    return 'La contraseña debe tener al menos 6 caracteres';
  }
  return null;
};

/**
 * Valida que dos contraseñas coincidan
 * @param {string} password
 * @param {string} confirmation
 * @returns {string|null}
 */
export const passwordMatch = (pwd, confirmation) => {
  if (pwd !== confirmation) {
    return 'Las contraseñas no coinciden';
  }
  return null;
};

/**
 * Ejecuta múltiples validaciones sobre un objeto de campos
 * @param {Object} fields - { fieldName: value }
 * @param {Object} rules - { fieldName: [validatorFn, ...] }
 * @returns {Object} - { fieldName: errorMessage | null }
 */
export const validateForm = (fields, rules) => {
  const errors = {};
  let isValid = true;

  Object.keys(rules).forEach((fieldName) => {
    const fieldRules = rules[fieldName];
    const value = fields[fieldName];

    for (const rule of fieldRules) {
      const error = rule(value);
      if (error) {
        errors[fieldName] = error;
        isValid = false;
        break;
      }
    }

    if (!errors[fieldName]) {
      errors[fieldName] = null;
    }
  });

  return { errors, isValid };
};
