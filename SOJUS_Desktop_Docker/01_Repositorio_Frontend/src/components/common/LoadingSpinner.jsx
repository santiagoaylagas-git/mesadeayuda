/**
 * LoadingSpinner — Indicador de carga con branding SOJUS
 */
import React from 'react';
import { View, Text, ActivityIndicator, StyleSheet } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { colors } from '../../theme/colors';

export default function LoadingSpinner({ message = 'Cargando...', fullScreen = true, size = 'large' }) {
  const content = (
    <View style={[styles.container, fullScreen && styles.fullScreen]}>
      <Ionicons name="shield-checkmark" size={40} color={colors.accent} style={styles.logo} />
      <ActivityIndicator size={size} color={colors.accent} />
      <Text style={styles.message}>{message}</Text>
    </View>
  );

  return content;
}

const styles = StyleSheet.create({
  container: {
    alignItems: 'center',
    justifyContent: 'center',
    padding: 40,
  },
  fullScreen: {
    flex: 1,
    backgroundColor: colors.background,
  },
  logo: {
    marginBottom: 16,
  },
  message: {
    marginTop: 12,
    fontSize: 14,
    color: colors.textSecondary,
  },
});
