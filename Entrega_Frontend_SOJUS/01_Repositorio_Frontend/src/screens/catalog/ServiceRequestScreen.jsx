/**
 * ServiceRequestScreen — Solicitar un servicio SOJUS
 */
import React, { useState } from 'react';
import { View, StyleSheet } from 'react-native';
import { colors } from '../../theme/colors';
import { catalogService } from '../../api';
import { getErrorMessage } from '../../utils/errorHandler';
import Input from '../../components/common/Input';
import Button from '../../components/common/Button';
import Toast from 'react-native-toast-message';

export default function ServiceRequestScreen({ route, navigation }) {
  const serviceName = route.params?.serviceName || 'Servicio';
  const [descripcion, setDescripcion] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async () => {
    if (!descripcion.trim()) {
      Toast.show({ type: 'error', text1: 'Error', text2: 'Describí tu solicitud' });
      return;
    }
    setLoading(true);
    try {
      await catalogService.createRequest({
        serviceId: route.params?.serviceId,
        descripcion: descripcion.trim(),
        tipo: serviceName,
      });
      Toast.show({ type: 'success', text1: 'Solicitud Enviada', text2: 'Tu solicitud fue registrada. Se creará un ticket automáticamente.' });
      navigation.goBack();
    } catch (error) {
      Toast.show({ type: 'error', text1: 'Error', text2: getErrorMessage(error) });
    } finally {
      setLoading(false);
    }
  };

  return (
    <View style={styles.container}>
      <View style={styles.form}>
        <Input
          label={`Solicitud: ${serviceName}`}
          value={descripcion}
          onChangeText={setDescripcion}
          placeholder="Describí el problema o solicitud en detalle..."
          multiline
          numberOfLines={6}
          icon="chatbox-outline"
          required
        />
        <Button
          title="Enviar Solicitud"
          onPress={handleSubmit}
          loading={loading}
          icon="send"
          fullWidth
        />
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: colors.background },
  form: { padding: 20 },
});
