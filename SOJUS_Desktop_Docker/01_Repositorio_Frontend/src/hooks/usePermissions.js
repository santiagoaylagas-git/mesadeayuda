/**
 * Hook de permisos RBAC para SOJUS
 * Verifica si el usuario actual tiene un permiso específico
 */
import { useMemo } from 'react';
import useAuth from './useAuth';
import { PERMISSIONS } from '../utils/constants';

/**
 * @returns {Object} { hasPermission, canAccess, userRole, isAdmin, isOperador, isTecnico }
 */
const usePermissions = () => {
  const { user } = useAuth();
  const userRole = user?.role || user?.roles?.[0] || null;

  const userPermissions = useMemo(() => {
    if (!userRole) return [];
    return PERMISSIONS[userRole] || [];
  }, [userRole]);

  /**
   * Verifica si el usuario tiene un permiso específico
   * @param {string} permission - ej: 'tickets.create'
   * @returns {boolean}
   */
  const hasPermission = (permission) => {
    return userPermissions.includes(permission);
  };

  /**
   * Verifica si el usuario puede acceder a una ruta/módulo
   * @param {string[]} requiredPermissions
   * @returns {boolean}
   */
  const canAccess = (requiredPermissions) => {
    return requiredPermissions.some((p) => userPermissions.includes(p));
  };

  return {
    hasPermission,
    canAccess,
    userRole,
    userPermissions,
    isAdmin: userRole === 'ADMINISTRADOR',
    isOperador: userRole === 'OPERADOR',
    isTecnico: userRole === 'TECNICO',
    isGestor: userRole === 'GESTOR',
    isAuditor: userRole === 'AUDITOR',
  };
};

export default usePermissions;
