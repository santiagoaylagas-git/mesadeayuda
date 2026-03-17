/**
 * Modal — Modal reutilizable SOJUS (confirm dialog)
 */
import React from 'react';
import {
  Modal as RNModal,
  View,
  Text,
  TouchableOpacity,
  StyleSheet,
  TouchableWithoutFeedback,
} from 'react-native';
import { colors } from '../../theme/colors';

export default function Modal({
  visible,
  title,
  message,
  onConfirm,
  onCancel,
  confirmText = 'Confirmar',
  cancelText = 'Cancelar',
  confirmVariant = 'danger',
  children,
}) {
  const confirmColor = confirmVariant === 'danger' ? colors.danger : colors.accent;

  return (
    <RNModal
      visible={visible}
      transparent
      animationType="fade"
      onRequestClose={onCancel}
    >
      <TouchableWithoutFeedback onPress={onCancel}>
        <View style={styles.overlay}>
          <TouchableWithoutFeedback>
            <View style={styles.container}>
              {title && <Text style={styles.title}>{title}</Text>}
              {message && <Text style={styles.message}>{message}</Text>}
              {children}
              <View style={styles.actions}>
                <TouchableOpacity
                  style={[styles.btn, styles.cancelBtn]}
                  onPress={onCancel}
                  accessibilityLabel={cancelText}
                >
                  <Text style={styles.cancelText}>{cancelText}</Text>
                </TouchableOpacity>
                <TouchableOpacity
                  style={[styles.btn, { backgroundColor: confirmColor }]}
                  onPress={onConfirm}
                  accessibilityLabel={confirmText}
                >
                  <Text style={styles.confirmText}>{confirmText}</Text>
                </TouchableOpacity>
              </View>
            </View>
          </TouchableWithoutFeedback>
        </View>
      </TouchableWithoutFeedback>
    </RNModal>
  );
}

const styles = StyleSheet.create({
  overlay: {
    flex: 1,
    backgroundColor: 'rgba(0,0,0,0.5)',
    justifyContent: 'center',
    alignItems: 'center',
    padding: 24,
  },
  container: {
    backgroundColor: colors.surface,
    borderRadius: 16,
    padding: 24,
    width: '100%',
    maxWidth: 400,
  },
  title: {
    fontSize: 18,
    fontWeight: '700',
    color: colors.textPrimary,
    marginBottom: 8,
  },
  message: {
    fontSize: 14,
    color: colors.textSecondary,
    lineHeight: 20,
    marginBottom: 20,
  },
  actions: {
    flexDirection: 'row',
    gap: 12,
  },
  btn: {
    flex: 1,
    paddingVertical: 12,
    borderRadius: 10,
    alignItems: 'center',
  },
  cancelBtn: {
    backgroundColor: colors.background,
    borderWidth: 1,
    borderColor: colors.border,
  },
  cancelText: {
    color: colors.textSecondary,
    fontWeight: '600',
    fontSize: 14,
  },
  confirmText: {
    color: colors.white,
    fontWeight: '600',
    fontSize: 14,
  },
});
