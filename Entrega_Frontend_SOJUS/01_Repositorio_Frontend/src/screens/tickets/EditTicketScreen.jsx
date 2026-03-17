/**
 * EditTicketScreen — Editar ticket existente SOJUS
 */
import React, { useState } from 'react';
import { View, StyleSheet } from 'react-native';
import { colors } from '../../theme/colors';
import { ticketService } from '../../api';
import { getErrorMessage } from '../../utils/errorHandler';
import { TicketForm } from '../../components';
import Toast from 'react-native-toast-message';

export default function EditTicketScreen({ route, navigation }) {
  const { ticket } = route.params;
  const [loading, setLoading] = useState(false);

  const handleUpdate = async (data) => {
    setLoading(true);
    try {
      await ticketService.update(ticket.id, data);
      Toast.show({ type: 'success', text1: 'Éxito', text2: 'Ticket actualizado correctamente' });
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
        initialValues={ticket}
        onSubmit={handleUpdate}
        isLoading={loading}
        submitLabel="Guardar Cambios"
        isEditing
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: colors.background },
});
