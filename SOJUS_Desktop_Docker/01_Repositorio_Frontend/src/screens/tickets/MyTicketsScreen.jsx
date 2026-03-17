/**
 * MyTicketsScreen — Tickets del usuario autenticado SOJUS
 */
import React, { useState, useCallback } from 'react';
import { View, FlatList, RefreshControl, StyleSheet } from 'react-native';
import { useFocusEffect } from '@react-navigation/native';
import { colors } from '../../theme/colors';
import { ticketService } from '../../api';
import { TicketCard, LoadingSpinner, EmptyState } from '../../components';

export default function MyTicketsScreen({ navigation }) {
  const [tickets, setTickets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);

  const load = async () => {
    try {
      const response = await ticketService.getMyTickets();
      setTickets(Array.isArray(response.data) ? response.data : response.data.content || []);
    } catch (e) { setTickets([]); }
    finally { setLoading(false); setRefreshing(false); }
  };

  useFocusEffect(useCallback(() => { load(); }, []));

  if (loading) return <LoadingSpinner message="Cargando mis tickets..." />;

  return (
    <View style={styles.container}>
      <FlatList
        data={tickets}
        keyExtractor={(item) => item.id.toString()}
        renderItem={({ item }) => (
          <TicketCard ticket={item} onPress={() => navigation.navigate('TicketDetail', { ticketId: item.id })} />
        )}
        contentContainerStyle={styles.list}
        refreshControl={<RefreshControl refreshing={refreshing} onRefresh={() => { setRefreshing(true); load(); }} />}
        ListEmptyComponent={
          <EmptyState icon="ticket-outline" title="Sin tickets" message="No tenés tickets asignados" />
        }
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: colors.background },
  list: { padding: 14, gap: 10 },
});
