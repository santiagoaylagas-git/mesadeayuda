/**
 * CreateTicketScreen — Crear nuevo ticket SOJUS
 */
import React, { useState } from 'react';
import { View, StyleSheet } from 'react-native';
import { colors } from '../../theme/colors';
import { ticketService } from '../../api';
import { getErrorMessage } from '../../utils/errorHandler';
import { TicketForm } from '../../components';
import Toast from 'react-native-toast-message';

export default function CreateTicketScreen({ navigation }) {
  const [loading, setLoading] = useState(false);

  const handleCreate = async (data) => {
    setLoading(true);
    try {
      await ticketService.create(data);
      Toast.show({ type: 'success', text1: 'Éxito', text2: 'Ticket creado correctamente' });
      navigation.goBack();
    } catch (error) {
      Toast.show({ type: 'error', text1: 'Error', text2: getErrorMessage(error) });
    } finally {
      setLoading(false);
    }
  };

  return (
    <View style={styles.container}>
      <TicketForm
        onSubmit={handleCreate}
        isLoading={loading}
        submitLabel="Crear Ticket"
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: colors.background },
});
