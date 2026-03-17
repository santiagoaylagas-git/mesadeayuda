/**
 * Toast — Configuración de react-native-toast-message para SOJUS
 * Este archivo exporta la configuración de toast personalizada
 */
import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { colors } from '../../theme/colors';

/**
 * Configuración de estilos de toast para react-native-toast-message
 * Se usa en App.jsx: <Toast config={toastConfig} />
 */
export const toastConfig = {
  success: ({ text1, text2 }) => (
    <View style={[styles.container, styles.success]}>
      <Ionicons name="checkmark-circle" size={20} color={colors.success} />
      <View style={styles.textWrap}>
        {text1 && <Text style={styles.title}>{text1}</Text>}
        {text2 && <Text style={styles.message}>{text2}</Text>}
      </View>
    </View>
  ),
  error: ({ text1, text2 }) => (
    <View style={[styles.container, styles.error]}>
      <Ionicons name="alert-circle" size={20} color={colors.danger} />
      <View style={styles.textWrap}>
        {text1 && <Text style={styles.title}>{text1}</Text>}
        {text2 && <Text style={styles.message}>{text2}</Text>}
      </View>
    </View>
  ),
  info: ({ text1, text2 }) => (
    <View style={[styles.container, styles.info]}>
      <Ionicons name="information-circle" size={20} color={colors.info} />
      <View style={styles.textWrap}>
        {text1 && <Text style={styles.title}>{text1}</Text>}
        {text2 && <Text style={styles.message}>{text2}</Text>}
      </View>
    </View>
  ),
};

const styles = StyleSheet.create({
  container: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 10,
    marginHorizontal: 16,
    padding: 14,
    borderRadius: 12,
    borderWidth: 1,
    width: '92%',
  },
  success: {
    backgroundColor: colors.successBg,
    borderColor: colors.success + '30',
  },
  error: {
    backgroundColor: colors.dangerBg,
    borderColor: colors.danger + '30',
  },
  info: {
    backgroundColor: colors.infoBg,
    borderColor: colors.info + '30',
  },
  textWrap: {
    flex: 1,
  },
  title: {
    fontSize: 14,
    fontWeight: '600',
    color: colors.textPrimary,
  },
  message: {
    fontSize: 12,
    color: colors.textSecondary,
    marginTop: 2,
  },
});
