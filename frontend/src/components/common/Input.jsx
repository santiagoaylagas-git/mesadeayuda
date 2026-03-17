/**
 * Input — Campo de entrada reutilizable SOJUS
 * @param {string} label
 * @param {string} value
 * @param {Function} onChangeText
 * @param {string} error - Mensaje de error
 * @param {string} placeholder
 * @param {string} icon - Nombre del ícono Ionicons
 * @param {boolean} multiline
 * @param {boolean} secureTextEntry
 */
import React, { useState } from 'react';
import { View, Text, TextInput, TouchableOpacity, StyleSheet } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { colors } from '../../theme/colors';

export default function Input({
  label,
  value,
  onChangeText,
  error,
  placeholder,
  icon,
  multiline = false,
  secureTextEntry = false,
  numberOfLines = 1,
  editable = true,
  onBlur,
  style,
  inputStyle,
  keyboardType = 'default',
  autoCapitalize = 'none',
  maxLength,
  required = false,
}) {
  const [isFocused, setIsFocused] = useState(false);
  const [showPassword, setShowPassword] = useState(false);

  const borderColor = error
    ? colors.danger
    : isFocused
    ? colors.accent
    : colors.border;

  return (
    <View style={[styles.container, style]}>
      {label && (
        <Text style={styles.label}>
          {label}
          {required && <Text style={styles.required}> *</Text>}
        </Text>
      )}
      <View style={[styles.inputWrapper, { borderColor }, multiline && styles.multiline]}>
        {icon && (
          <Ionicons name={icon} size={18} color={colors.textSecondary} style={styles.icon} />
        )}
        <TextInput
          style={[
            styles.input,
            multiline && { minHeight: 100, textAlignVertical: 'top' },
            inputStyle,
          ]}
          value={value}
          onChangeText={onChangeText}
          placeholder={placeholder}
          placeholderTextColor={colors.textDisabled}
          multiline={multiline}
          numberOfLines={multiline ? numberOfLines : 1}
          secureTextEntry={secureTextEntry && !showPassword}
          editable={editable}
          onFocus={() => setIsFocused(true)}
          onBlur={() => {
            setIsFocused(false);
            onBlur?.();
          }}
          keyboardType={keyboardType}
          autoCapitalize={autoCapitalize}
          maxLength={maxLength}
          accessibilityLabel={label || placeholder}
        />
        {secureTextEntry && (
          <TouchableOpacity
            onPress={() => setShowPassword(!showPassword)}
            accessibilityLabel={showPassword ? 'Ocultar contraseña' : 'Mostrar contraseña'}
          >
            <Ionicons
              name={showPassword ? 'eye-off-outline' : 'eye-outline'}
              size={20}
              color={colors.textSecondary}
            />
          </TouchableOpacity>
        )}
      </View>
      {error && <Text style={styles.error}>{error}</Text>}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    marginBottom: 16,
  },
  label: {
    fontSize: 13,
    fontWeight: '600',
    color: colors.textSecondary,
    textTransform: 'uppercase',
    letterSpacing: 0.5,
    marginBottom: 6,
  },
  required: {
    color: colors.danger,
  },
  inputWrapper: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: colors.surface,
    borderRadius: 10,
    borderWidth: 1,
    paddingHorizontal: 14,
  },
  multiline: {
    alignItems: 'flex-start',
    paddingVertical: 10,
  },
  icon: {
    marginRight: 10,
  },
  input: {
    flex: 1,
    height: 48,
    color: colors.textPrimary,
    fontSize: 15,
  },
  error: {
    color: colors.danger,
    fontSize: 12,
    marginTop: 4,
  },
});
