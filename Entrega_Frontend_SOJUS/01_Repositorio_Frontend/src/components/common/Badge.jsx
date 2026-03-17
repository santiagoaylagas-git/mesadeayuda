/**
 * Badge — Etiqueta visual con color semántico SOJUS
 */
import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { colors } from '../../theme/colors';

export default function Badge({ label, color, bgColor, borderColor, size = 'md', style }) {
  const sizeStyles = size === 'sm' ? styles.sm : styles.md;

  return (
    <View
      style={[
        styles.base,
        sizeStyles,
        {
          backgroundColor: bgColor || (color ? color + '15' : colors.background),
          borderColor: borderColor || (color ? color + '25' : colors.border),
        },
        style,
      ]}
      accessibilityLabel={label}
    >
      <Text
        style={[
          styles.text,
          size === 'sm' && styles.textSm,
          { color: color || colors.textSecondary },
        ]}
      >
        {label}
      </Text>
    </View>
  );
}

const styles = StyleSheet.create({
  base: {
    borderRadius: 6,
    borderWidth: 1,
    alignSelf: 'flex-start',
  },
  sm: {
    paddingHorizontal: 6,
    paddingVertical: 2,
  },
  md: {
    paddingHorizontal: 10,
    paddingVertical: 4,
  },
  text: {
    fontSize: 12,
    fontWeight: '600',
  },
  textSm: {
    fontSize: 10,
  },
});
