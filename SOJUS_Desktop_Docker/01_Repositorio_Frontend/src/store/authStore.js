/**
 * AuthStore — Estado global de autenticación para SOJUS
 * Context API + useReducer
 */
import React, { createContext, useReducer, useEffect, useCallback } from 'react';
import { authService, setUnauthorizedCallback } from '../api';
import { saveToken, saveUserData, clearSession, getToken, getUserData } from '../utils/storage';

// Estado inicial
const initialState = {
  user: null,
  token: null,
  isAuthenticated: false,
  isLoading: true,
};

// Acciones
const AUTH_ACTIONS = {
  LOGIN: 'LOGIN',
  LOGOUT: 'LOGOUT',
  SET_LOADING: 'SET_LOADING',
  RESTORE_SESSION: 'RESTORE_SESSION',
  UPDATE_PROFILE: 'UPDATE_PROFILE',
};

// Reducer
const authReducer = (state, action) => {
  switch (action.type) {
    case AUTH_ACTIONS.LOGIN:
      return {
        ...state,
        user: action.payload.user,
        token: action.payload.token,
        isAuthenticated: true,
        isLoading: false,
      };
    case AUTH_ACTIONS.LOGOUT:
      return {
        ...state,
        user: null,
        token: null,
        isAuthenticated: false,
        isLoading: false,
      };
    case AUTH_ACTIONS.SET_LOADING:
      return { ...state, isLoading: action.payload };
    case AUTH_ACTIONS.RESTORE_SESSION:
      return {
        ...state,
        user: action.payload.user,
        token: action.payload.token,
        isAuthenticated: true,
        isLoading: false,
      };
    case AUTH_ACTIONS.UPDATE_PROFILE:
      return { ...state, user: { ...state.user, ...action.payload } };
    default:
      return state;
  }
};

// Contexto
export const AuthContext = createContext(null);

/**
 * Provider de autenticación
 */
export const AuthProvider = ({ children }) => {
  const [state, dispatch] = useReducer(authReducer, initialState);

  // Restaurar sesión al iniciar la app
  useEffect(() => {
    const restoreSession = async () => {
      try {
        const token = await getToken();
        const user = await getUserData();
        if (token && user) {
          dispatch({
            type: AUTH_ACTIONS.RESTORE_SESSION,
            payload: { token, user },
          });
        } else {
          dispatch({ type: AUTH_ACTIONS.SET_LOADING, payload: false });
        }
      } catch (error) {
        dispatch({ type: AUTH_ACTIONS.SET_LOADING, payload: false });
      }
    };
    restoreSession();
  }, []);

  // Configurar callback de 401 para auto-logout
  useEffect(() => {
    setUnauthorizedCallback(() => {
      dispatch({ type: AUTH_ACTIONS.LOGOUT });
    });
  }, []);

  /**
   * Iniciar sesión
   */
  const login = useCallback(async (username, password) => {
    try {
      const response = await authService.login(username, password);
      const { token, ...userData } = response.data;

      await saveToken(token);
      await saveUserData(userData);

      dispatch({
        type: AUTH_ACTIONS.LOGIN,
        payload: { token, user: userData },
      });

      return { success: true };
    } catch (error) {
      const message = error.response?.data?.message || 'Credenciales inválidas';
      return { success: false, error: message };
    }
  }, []);

  /**
   * Cerrar sesión
   */
  const logout = useCallback(async () => {
    try {
      await authService.logout();
    } catch (_) {
      // Ignorar errores de logout en el servidor
    } finally {
      await clearSession();
      dispatch({ type: AUTH_ACTIONS.LOGOUT });
    }
  }, []);

  /**
   * Cambiar contraseña
   */
  const changePassword = useCallback(async (currentPassword, newPassword) => {
    const response = await authService.changePassword(currentPassword, newPassword);
    return response.data;
  }, []);

  const value = {
    ...state,
    login,
    logout,
    changePassword,
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};
