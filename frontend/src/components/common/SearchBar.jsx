/**
 * SearchBar — Barra de búsqueda con debounce SOJUS
 */
import React, { useState, useEffect, useRef } from 'react';
import { View, TextInput, TouchableOpacity, StyleSheet } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { colors } from '../../theme/colors';

export default function SearchBar({
  placeholder = 'Buscar...',
  onSearch,
  value: externalValue,
  debounceMs = 400,
  style,
}) {
  const [internalValue, setInternalValue] = useState(externalValue || '');
  const timerRef = useRef(null);

  useEffect(() => {
    if (externalValue !== undefined) {
      setInternalValue(externalValue);
    }
  }, [externalValue]);

  const handleChange = (text) => {
    setInternalValue(text);
    if (timerRef.current) clearTimeout(timerRef.current);
    timerRef.current = setTimeout(() => {
      onSearch?.(text);
    }, debounceMs);
  };

  const handleClear = () => {
    setInternalValue('');
    onSearch?.('');
  };

  return (
    <View style={[styles.container, style]}>
      <Ionicons name="search-outline" size={18} color={colors.textSecondary} />
      <TextInput
        style={styles.input}
        value={internalValue}
        onChangeText={handleChange}
        placeholder={placeholder}
        placeholderTextColor={colors.textDisabled}
        autoCapitalize="none"
        autoCorrect={false}
        accessibilityLabel="Buscar"
      />
      {internalValue.length > 0 && (
        <TouchableOpacity onPress={handleClear} accessibilityLabel="Limpiar búsqueda">
          <Ionicons name="close-circle" size={18} color={colors.textSecondary} />
        </TouchableOpacity>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: colors.surface,
    borderRadius: 10,
    borderWidth: 1,
    borderColor: colors.border,
    paddingHorizontal: 12,
    gap: 8,
    height: 44,
  },
  input: {
    flex: 1,
    fontSize: 14,
    color: colors.textPrimary,
  },
});
