/**
 * Hook de autenticación para SOJUS
 * Acceso al AuthContext desde cualquier componente
 */
import { useContext } from 'react';
import { AuthContext } from '../store/authStore';

const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth debe usarse dentro de un AuthProvider');
  }
  return context;
};

export default useAuth;
