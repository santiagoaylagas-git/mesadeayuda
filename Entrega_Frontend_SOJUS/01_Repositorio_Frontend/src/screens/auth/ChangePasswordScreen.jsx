/**
 * ChangePasswordScreen — Cambiar contraseña SOJUS
 */
import React, { useState } from 'react';
import { View, StyleSheet, Alert } from 'react-native';
import { colors } from '../../theme/colors';
import useAuth from '../../hooks/useAuth';
import Input from '../../components/common/Input';
import Button from '../../components/common/Button';
import { getErrorMessage } from '../../utils/errorHandler';
import Toast from 'react-native-toast-message';

export default function ChangePasswordScreen({ navigation }) {
  const { changePassword } = useAuth();
  const [currentPassword, setCurrentPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState({});

  const validate = () => {
    const e = {};
    if (!currentPassword) e.current = 'Ingresá tu contraseña actual';
    if (!newPassword) e.new = 'Ingresá la nueva contraseña';
    else if (newPassword.length < 6) e.new = 'Mínimo 6 caracteres';
    if (newPassword !== confirmPassword) e.confirm = 'Las contraseñas no coinciden';
    setErrors(e);
    return Object.keys(e).length === 0;
  };

  const handleSubmit = async () => {
    if (!validate()) return;
    setLoading(true);
    try {
      await changePassword(currentPassword, newPassword);
      Toast.show({ type: 'success', text1: 'Éxito', text2: 'Contraseña actualizada correctamente' });
      navigation.goBack();
    } catch (error) {
      setErrors({ current: getErrorMessage(error) });
    } finally {
      setLoading(false);
    }
  };

  return (
    <View style={styles.container}>
      <View style={styles.form}>
        <Input
          label="Contraseña Actual"
          value={currentPassword}
          onChangeText={setCurrentPassword}
          secureTextEntry
          error={errors.current}
          icon="lock-closed-outline"
          required
        />
        <Input
          label="Nueva Contraseña"
          value={newPassword}
          onChangeText={setNewPassword}
          secureTextEntry
          error={errors.new}
          icon="key-outline"
          required
        />
        <Input
          label="Confirmar Nueva Contraseña"
          value={confirmPassword}
          onChangeText={setConfirmPassword}
          secureTextEntry
          error={errors.confirm}
          icon="key-outline"
          required
        />
        <Button
          title="Cambiar Contraseña"
          onPress={handleSubmit}
          loading={loading}
          fullWidth
          icon="checkmark-circle-outline"
        />
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: colors.background },
  form: { padding: 20 },
});
