/**
 * Card — Contenedor tipo tarjeta SOJUS
 */
import React from 'react';
import { View, TouchableOpacity, StyleSheet } from 'react-native';
import { colors } from '../../theme/colors';
import { shadows } from '../../theme/spacing';

export default function Card({ children, onPress, style, noPadding = false }) {
  const Container = onPress ? TouchableOpacity : View;
  const props = onPress ? { onPress, activeOpacity: 0.7 } : {};

  return (
    <Container
      style={[styles.card, !noPadding && styles.padding, style]}
      {...props}
    >
      {children}
    </Container>
  );
}

const styles = StyleSheet.create({
  card: {
    backgroundColor: colors.surface,
    borderRadius: 14,
    borderWidth: 1,
    borderColor: colors.border,
    ...shadows.sm,
  },
  padding: {
    padding: 16,
  },
});
