/**
 * UIStore — Estado global de interfaz para SOJUS
 * Manejo de loading, toasts y modales
 */
import React, { createContext, useReducer, useCallback } from 'react';

const initialState = {
  isGlobalLoading: false,
  toast: null,
  confirmModal: null,
};

const UI_ACTIONS = {
  SET_GLOBAL_LOADING: 'SET_GLOBAL_LOADING',
  SHOW_TOAST: 'SHOW_TOAST',
  HIDE_TOAST: 'HIDE_TOAST',
  SHOW_CONFIRM: 'SHOW_CONFIRM',
  HIDE_CONFIRM: 'HIDE_CONFIRM',
};

const uiReducer = (state, action) => {
  switch (action.type) {
    case UI_ACTIONS.SET_GLOBAL_LOADING:
      return { ...state, isGlobalLoading: action.payload };
    case UI_ACTIONS.SHOW_TOAST:
      return { ...state, toast: action.payload };
    case UI_ACTIONS.HIDE_TOAST:
      return { ...state, toast: null };
    case UI_ACTIONS.SHOW_CONFIRM:
      return { ...state, confirmModal: action.payload };
    case UI_ACTIONS.HIDE_CONFIRM:
      return { ...state, confirmModal: null };
    default:
      return state;
  }
};

export const UIContext = createContext(null);

export const UIProvider = ({ children }) => {
  const [state, dispatch] = useReducer(uiReducer, initialState);

  const setGlobalLoading = useCallback((loading) => {
    dispatch({ type: UI_ACTIONS.SET_GLOBAL_LOADING, payload: loading });
  }, []);

  /** Mostrar un toast de éxito */
  const showSuccess = useCallback((message) => {
    dispatch({
      type: UI_ACTIONS.SHOW_TOAST,
      payload: { type: 'success', text1: 'Éxito', text2: message },
    });
  }, []);

  /** Mostrar un toast de error */
  const showError = useCallback((message) => {
    dispatch({
      type: UI_ACTIONS.SHOW_TOAST,
      payload: { type: 'error', text1: 'Error', text2: message },
    });
  }, []);

  /** Mostrar un toast informativo */
  const showInfo = useCallback((message) => {
    dispatch({
      type: UI_ACTIONS.SHOW_TOAST,
      payload: { type: 'info', text1: 'Información', text2: message },
    });
  }, []);

  /** Mostrar modal de confirmación */
  const showConfirm = useCallback(({ title, message, onConfirm, onCancel, confirmText, cancelText }) => {
    dispatch({
      type: UI_ACTIONS.SHOW_CONFIRM,
      payload: {
        title: title || '¿Estás seguro?',
        message,
        onConfirm,
        onCancel,
        confirmText: confirmText || 'Confirmar',
        cancelText: cancelText || 'Cancelar',
      },
    });
  }, []);

  const hideConfirm = useCallback(() => {
    dispatch({ type: UI_ACTIONS.HIDE_CONFIRM });
  }, []);

  const value = {
    ...state,
    setGlobalLoading,
    showSuccess,
    showError,
    showInfo,
    showConfirm,
    hideConfirm,
  };

  return (
    <UIContext.Provider value={value}>
      {children}
    </UIContext.Provider>
  );
};
