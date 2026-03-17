/**
 * Wrapper de almacenamiento seguro para SOJUS
 * Usa expo-secure-store para datos sensibles (JWT)
 */
import * as SecureStore from 'expo-secure-store';
import { STORAGE_KEYS } from './constants';

/**
 * Guarda el token JWT de forma segura
 * @param {string} token
 */
export const saveToken = async (token) => {
  await SecureStore.setItemAsync(STORAGE_KEYS.JWT_TOKEN, token);
};

/**
 * Obtiene el token JWT almacenado
 * @returns {Promise<string|null>}
 */
export const getToken = async () => {
  return await SecureStore.getItemAsync(STORAGE_KEYS.JWT_TOKEN);
};

/**
 * Elimina el token JWT
 */
export const removeToken = async () => {
  await SecureStore.deleteItemAsync(STORAGE_KEYS.JWT_TOKEN);
};

/**
 * Guarda los datos del usuario
 * @param {Object} userData
 */
export const saveUserData = async (userData) => {
  await SecureStore.setItemAsync(STORAGE_KEYS.USER_DATA, JSON.stringify(userData));
};

/**
 * Obtiene los datos del usuario almacenados
 * @returns {Promise<Object|null>}
 */
export const getUserData = async () => {
  const data = await SecureStore.getItemAsync(STORAGE_KEYS.USER_DATA);
  return data ? JSON.parse(data) : null;
};

/**
 * Elimina los datos del usuario
 */
export const removeUserData = async () => {
  await SecureStore.deleteItemAsync(STORAGE_KEYS.USER_DATA);
};

/**
 * Limpia toda la sesión (token + datos de usuario)
 */
export const clearSession = async () => {
  await Promise.all([
    removeToken(),
    removeUserData(),
  ]);
};
