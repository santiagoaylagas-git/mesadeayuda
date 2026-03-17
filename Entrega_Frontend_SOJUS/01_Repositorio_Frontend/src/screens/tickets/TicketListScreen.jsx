/**
 * TicketListScreen — Lista de tickets con filtros, búsqueda y paginación SOJUS
 */
import React, { useState, useCallback } from 'react';
import {
  View, Text, StyleSheet, FlatList, TouchableOpacity,
  RefreshControl,
} from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { useFocusEffect } from '@react-navigation/native';
import { colors } from '../../theme/colors';
import { TICKET_STATUS } from '../../utils/constants';
import { ticketService } from '../../api';
import usePermissions from '../../hooks/usePermissions';
import { TicketCard, SearchBar, EmptyState } from '../../components';

export default function TicketListScreen({ navigation }) {
  const { hasPermission } = usePermissions();
  const [tickets, setTickets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [activeFilter, setActiveFilter] = useState(null);
  const [searchQuery, setSearchQuery] = useState('');

  const loadTickets = async () => {
    try {
      const params = {};
      if (activeFilter) params.status = activeFilter;
      if (searchQuery) params.search = searchQuery;
      const response = await ticketService.getAll(params);
      const data = response.data.content || response.data;
      setTickets(Array.isArray(data) ? data : []);
    } catch (error) {
      setTickets([]);
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  };

  useFocusEffect(useCallback(() => { setLoading(true); loadTickets(); }, [activeFilter, searchQuery]));

  const onRefresh = () => { setRefreshing(true); loadTickets(); };

  const filters = [
    { key: null, label: 'Todos' },
    ...Object.values(TICKET_STATUS).map((s) => ({ key: s.key, label: s.label })),
  ];

  const filteredTickets = tickets;

  return (
    <View style={styles.container}>
      {/* Search */}
      <View style={styles.searchRow}>
        <SearchBar
          placeholder="Buscar tickets..."
          onSearch={setSearchQuery}
          style={styles.searchBar}
        />
        {hasPermission('tickets.create') && (
          <TouchableOpacity
            style={styles.createBtn}
            onPress={() => navigation.navigate('CreateTicket')}
            accessibilityLabel="Crear nuevo ticket"
          >
            <Ionicons name="add" size={20} color="#fff" />
          </TouchableOpacity>
        )}
      </View>

      {/* Filter chips */}
      <FlatList
        horizontal
        data={filters}
        keyExtractor={(item) => item.key || 'all'}
        renderItem={({ item }) => (
          <TouchableOpacity
            style={[styles.chip, activeFilter === item.key && styles.chipActive]}
            onPress={() => setActiveFilter(item.key)}
          >
            <Text style={[styles.chipText, activeFilter === item.key && styles.chipTextActive]}>
              {item.label}
            </Text>
          </TouchableOpacity>
        )}
        contentContainerStyle={styles.chipRow}
        showsHorizontalScrollIndicator={false}
      />

      {/* Ticket list */}
      <FlatList
        data={filteredTickets}
        keyExtractor={(item) => item.id.toString()}
        renderItem={({ item }) => (
          <TicketCard
            ticket={item}
            onPress={() => navigation.navigate('TicketDetail', { ticketId: item.id })}
          />
        )}
        contentContainerStyle={styles.listContent}
        refreshControl={<RefreshControl refreshing={refreshing} onRefresh={onRefresh} />}
        ListHeaderComponent={
          <Text style={styles.countText}>{filteredTickets.length} ticket(s)</Text>
        }
        ListEmptyComponent={
          !loading ? (
            <EmptyState
              icon="ticket-outline"
              title="Sin tickets"
              message={activeFilter ? 'No hay tickets con este filtro' : 'No hay tickets registrados'}
              actionLabel={hasPermission('tickets.create') ? 'Crear Ticket' : undefined}
              onAction={hasPermission('tickets.create') ? () => navigation.navigate('CreateTicket') : undefined}
            />
          ) : null
        }
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: colors.background },
  searchRow: {
    flexDirection: 'row', paddingHorizontal: 14, paddingTop: 12,
    paddingBottom: 8, gap: 10, alignItems: 'center',
  },
  searchBar: { flex: 1 },
  createBtn: {
    width: 44, height: 44, borderRadius: 10, backgroundColor: colors.accent,
    justifyContent: 'center', alignItems: 'center',
  },
  chipRow: { paddingHorizontal: 14, paddingBottom: 8, gap: 8 },
  chip: {
    paddingHorizontal: 14, paddingVertical: 8, borderRadius: 20,
    backgroundColor: colors.surface, borderWidth: 1, borderColor: colors.border,
  },
  chipActive: { backgroundColor: colors.accent, borderColor: colors.accent },
  chipText: { fontSize: 12, color: colors.textSecondary, fontWeight: '500' },
  chipTextActive: { color: colors.white },
  listContent: { padding: 14, gap: 10 },
  countText: { fontSize: 12, color: colors.textSecondary, marginBottom: 4 },
});
