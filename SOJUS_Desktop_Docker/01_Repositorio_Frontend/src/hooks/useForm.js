/**
 * Hook de formularios para SOJUS
 * Manejo genérico de campos, errores y validación
 */
import { useState, useCallback } from 'react';

/**
 * @param {Object} initialValues - Valores iniciales del formulario
 * @param {Function} validateFn - Función de validación (values) => { errors, isValid }
 * @returns {Object}
 */
const useForm = (initialValues = {}, validateFn = null) => {
  const [values, setValues] = useState(initialValues);
  const [errors, setErrors] = useState({});
  const [touched, setTouched] = useState({});
  const [isSubmitting, setIsSubmitting] = useState(false);

  /** Actualizar un campo */
  const setValue = useCallback((field, value) => {
    setValues((prev) => ({ ...prev, [field]: value }));
    // Limpiar error del campo al editarlo
    if (errors[field]) {
      setErrors((prev) => ({ ...prev, [field]: null }));
    }
  }, [errors]);

  /** Marcar un campo como tocado (para mostrar errores) */
  const setFieldTouched = useCallback((field) => {
    setTouched((prev) => ({ ...prev, [field]: true }));
  }, []);

  /** Validar todos los campos */
  const validate = useCallback(() => {
    if (!validateFn) return true;
    const result = validateFn(values);
    setErrors(result.errors || {});
    // Marcar todos los campos como tocados
    const allTouched = {};
    Object.keys(values).forEach((key) => { allTouched[key] = true; });
    setTouched(allTouched);
    return result.isValid;
  }, [values, validateFn]);

  /** Submit handler */
  const handleSubmit = useCallback(async (onSubmit) => {
    const isValid = validate();
    if (!isValid) return false;

    setIsSubmitting(true);
    try {
      await onSubmit(values);
      return true;
    } catch (error) {
      throw error;
    } finally {
      setIsSubmitting(false);
    }
  }, [values, validate]);

  /** Resetear el formulario */
  const reset = useCallback(() => {
    setValues(initialValues);
    setErrors({});
    setTouched({});
    setIsSubmitting(false);
  }, [initialValues]);

  /** Obtener error visible de un campo (solo si fue tocado) */
  const getFieldError = useCallback((field) => {
    return touched[field] ? errors[field] : null;
  }, [errors, touched]);

  return {
    values,
    errors,
    touched,
    isSubmitting,
    setValue,
    setFieldTouched,
    validate,
    handleSubmit,
    reset,
    getFieldError,
    setValues,
    setErrors,
  };
};

export default useForm;
