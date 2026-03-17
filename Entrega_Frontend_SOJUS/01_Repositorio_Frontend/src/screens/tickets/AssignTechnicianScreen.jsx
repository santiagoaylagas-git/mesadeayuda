/**
 * AssignTechnicianScreen — Asignar técnico a un ticket SOJUS
 */
import React, { useState, useEffect } from 'react';
import { View, Text, FlatList, TouchableOpacity, StyleSheet } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { colors } from '../../theme/colors';
import { userService, ticketService } from '../../api';
import { getErrorMessage } from '../../utils/errorHandler';
import { LoadingSpinner, SearchBar, EmptyState } from '../../components';
import Toast from 'react-native-toast-message';

export default function AssignTechnicianScreen({ route, navigation }) {
  const { ticketId } = route.params;
  const [technicians, setTechnicians] = useState([]);
  const [filtered, setFiltered] = useState([]);
  const [loading, setLoading] = useState(true);
  const [assigning, setAssigning] = useState(null);

  useEffect(() => {
    loadTechnicians();
  }, []);

  const loadTechnicians = async () => {
    try {
      const response = await userService.getTechnicians();
      const data = Array.isArray(response.data) ? response.data : [];
      setTechnicians(data);
      setFiltered(data);
    } catch (error) {
      Toast.show({ type: 'error', text1: 'Error', text2: getErrorMessage(error) });
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = (query) => {
    if (!query) {
      setFiltered(technicians);
    } else {
      setFiltered(technicians.filter((t) =>
        (t.fullName || t.username || '').toLowerCase().includes(query.toLowerCase())
      ));
    }
  };

  const handleAssign = async (technicianId) => {
    setAssigning(technicianId);
    try {
      await ticketService.assign(ticketId, { tecnicoId: technicianId });
      Toast.show({ type: 'success', text1: 'Éxito', text2: 'Técnico asignado correctamente' });
      navigation.goBack();
    } catch (error) {
      Toast.show({ type: 'error', text1: 'Error', text2: getErrorMessage(error) });
    } finally {
      setAssigning(null);
    }
  };

  if (loading) return <LoadingSpinner message="Cargando técnicos..." />;

  return (
    <View style={styles.container}>
      <SearchBar
        placeholder="Buscar técnico..."
        onSearch={handleSearch}
        style={styles.search}
      />
      <FlatList
        data={filtered}
        keyExtractor={(item) => item.id.toString()}
        renderItem={({ item }) => (
          <TouchableOpacity
            style={styles.card}
            onPress={() => handleAssign(item.id)}
            disabled={assigning !== null}
            accessibilityLabel={`Asignar a ${item.fullName || item.username}`}
          >
            <View style={styles.avatar}>
              <Text style={styles.avatarText}>
                {(item.fullName || item.username || '??')[0].toUpperCase()}
              </Text>
            </View>
            <View style={styles.info}>
              <Text style={styles.name}>{item.fullName || item.username}</Text>
              <Text style={styles.email}>{item.email || 'Sin email'}</Text>
            </View>
            {assigning === item.id ? (
              <Text style={styles.assigning}>Asignando...</Text>
            ) : (
              <Ionicons name="arrow-forward-circle-outline" size={24} color={colors.accent} />
            )}
          </TouchableOpacity>
        )}
        contentContainerStyle={styles.list}
        ListEmptyComponent={
          <EmptyState icon="people-outline" title="Sin técnicos" message="No hay técnicos disponibles" />
        }
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: colors.background },
  search: { margin: 14 },
  list: { paddingHorizontal: 14, gap: 8 },
  card: {
    flexDirection: 'row', alignItems: 'center', gap: 12,
    backgroundColor: colors.surface, padding: 14, borderRadius: 12,
    borderWidth: 1, borderColor: colors.border,
  },
  avatar: {
    width: 40, height: 40, borderRadius: 20,
    backgroundColor: colors.accent + '15', justifyContent: 'center', alignItems: 'center',
  },
  avatarText: { fontSize: 16, fontWeight: '700', color: colors.accent },
  info: { flex: 1 },
  name: { fontSize: 14, fontWeight: '600', color: colors.textPrimary },
  email: { fontSize: 12, color: colors.textSecondary },
  assigning: { fontSize: 12, color: colors.accent, fontWeight: '500' },
});
