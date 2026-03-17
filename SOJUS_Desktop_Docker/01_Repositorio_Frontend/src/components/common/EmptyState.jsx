/**
 * EmptyState — Pantalla para listas vacías SOJUS
 */
import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { colors } from '../../theme/colors';
import Button from './Button';

export default function EmptyState({
  icon = 'document-outline',
  title = 'Sin resultados',
  message = 'No hay datos para mostrar',
  actionLabel,
  onAction,
}) {
  return (
    <View style={styles.container} accessibilityLabel={title}>
      <Ionicons name={icon} size={56} color={colors.border} />
      <Text style={styles.title}>{title}</Text>
      <Text style={styles.message}>{message}</Text>
      {actionLabel && onAction && (
        <Button
          title={actionLabel}
          onPress={onAction}
          variant="outline"
          size="sm"
          style={styles.action}
        />
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    alignItems: 'center',
    justifyContent: 'center',
    paddingVertical: 60,
    paddingHorizontal: 32,
  },
  title: {
    fontSize: 16,
    fontWeight: '600',
    color: colors.textPrimary,
    marginTop: 16,
  },
  message: {
    fontSize: 13,
    color: colors.textSecondary,
    textAlign: 'center',
    marginTop: 4,
    lineHeight: 20,
  },
  action: {
    marginTop: 16,
  },
});
