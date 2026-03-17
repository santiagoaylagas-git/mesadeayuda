/**
 * Button — Botón reutilizable SOJUS
 * @param {string} title - Texto del botón
 * @param {Function} onPress
 * @param {string} variant - 'primary' | 'secondary' | 'danger' | 'outline' | 'ghost'
 * @param {string} size - 'sm' | 'md' | 'lg'
 * @param {boolean} loading - Muestra indicador de carga
 * @param {boolean} disabled
 * @param {string} icon - Nombre del ícono Ionicons
 * @param {Object} style - Estilos adicionales
 */
import React from 'react';
import { TouchableOpacity, Text, ActivityIndicator, StyleSheet, View } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { colors } from '../../theme/colors';

const VARIANTS = {
  primary: {
    bg: colors.primary[500],
    text: colors.white,
    border: colors.primary[500],
  },
  secondary: {
    bg: colors.accent,
    text: colors.white,
    border: colors.accent,
  },
  danger: {
    bg: colors.danger,
    text: colors.white,
    border: colors.danger,
  },
  outline: {
    bg: 'transparent',
    text: colors.primary[500],
    border: colors.primary[500],
  },
  ghost: {
    bg: 'transparent',
    text: colors.textSecondary,
    border: 'transparent',
  },
};

const SIZES = {
  sm: { paddingVertical: 8, paddingHorizontal: 14, fontSize: 13, iconSize: 16 },
  md: { paddingVertical: 12, paddingHorizontal: 20, fontSize: 15, iconSize: 18 },
  lg: { paddingVertical: 14, paddingHorizontal: 24, fontSize: 16, iconSize: 20 },
};

export default function Button({
  title,
  onPress,
  variant = 'primary',
  size = 'md',
  loading = false,
  disabled = false,
  icon,
  style,
  textStyle,
  fullWidth = false,
}) {
  const v = VARIANTS[variant] || VARIANTS.primary;
  const s = SIZES[size] || SIZES.md;
  const isDisabled = disabled || loading;

  return (
    <TouchableOpacity
      style={[
        styles.base,
        {
          backgroundColor: v.bg,
          borderColor: v.border,
          paddingVertical: s.paddingVertical,
          paddingHorizontal: s.paddingHorizontal,
        },
        fullWidth && styles.fullWidth,
        isDisabled && styles.disabled,
        style,
      ]}
      onPress={onPress}
      disabled={isDisabled}
      activeOpacity={0.7}
      accessibilityLabel={title}
      accessibilityRole="button"
      accessibilityState={{ disabled: isDisabled }}
    >
      {loading ? (
        <ActivityIndicator color={v.text} size="small" />
      ) : (
        <View style={styles.content}>
          {icon && <Ionicons name={icon} size={s.iconSize} color={v.text} style={styles.icon} />}
          <Text style={[styles.text, { color: v.text, fontSize: s.fontSize }, textStyle]}>
            {title}
          </Text>
        </View>
      )}
    </TouchableOpacity>
  );
}

const styles = StyleSheet.create({
  base: {
    borderRadius: 10,
    borderWidth: 1,
    alignItems: 'center',
    justifyContent: 'center',
    flexDirection: 'row',
  },
  fullWidth: {
    width: '100%',
  },
  disabled: {
    opacity: 0.6,
  },
  content: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 8,
  },
  text: {
    fontWeight: '600',
  },
  icon: {
    marginRight: 2,
  },
});
