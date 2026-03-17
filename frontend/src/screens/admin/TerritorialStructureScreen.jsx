/**
 * TerritorialStructureScreen — Estructura territorial (Admin) SOJUS
 */
import React, { useState, useCallback } from 'react';
import { View, Text, FlatList, RefreshControl, StyleSheet } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { useFocusEffect } from '@react-navigation/native';
import { colors } from '../../theme/colors';
import apiClient from '../../api/client';
import { LoadingSpinner, EmptyState, SearchBar } from '../../components';

export default function TerritorialStructureScreen() {
  const [locations, setLocations] = useState([]);
  const [filtered, setFiltered] = useState([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);

  const load = async () => {
    try {
      const response = await apiClient.get('/api/locations');
      const data = Array.isArray(response.data) ? response.data : response.data.content || [];
      setLocations(data);
      setFiltered(data);
    } catch (e) { setLocations([]); setFiltered([]); }
    finally { setLoading(false); setRefreshing(false); }
  };

  useFocusEffect(useCallback(() => { load(); }, []));

  const handleSearch = (query) => {
    if (!query) { setFiltered(locations); return; }
    setFiltered(locations.filter((l) =>
      (l.nombre || l.name || '').toLowerCase().includes(query.toLowerCase())
    ));
  };

  if (loading) return <LoadingSpinner message="Cargando estructura territorial..." />;

  return (
    <View style={styles.container}>
      <SearchBar placeholder="Buscar juzgado o dependencia..." onSearch={handleSearch} style={styles.search} />
      <FlatList
        data={filtered}
        keyExtractor={(item) => item.id.toString()}
        contentContainerStyle={styles.list}
        refreshControl={<RefreshControl refreshing={refreshing} onRefresh={() => { setRefreshing(true); load(); }} />}
        renderItem={({ item }) => (
          <View style={styles.card}>
            <View style={styles.iconBg}>
              <Ionicons name="business-outline" size={20} color={colors.accent} />
            </View>
            <View style={styles.info}>
              <Text style={styles.name}>{item.nombre || item.name}</Text>
              {item.circunscripcion && <Text style={styles.sub}>Circunscripción: {item.circunscripcion}</Text>}
              {item.direccion && <Text style={styles.address}>{item.direccion}</Text>}
            </View>
          </View>
        )}
        ListEmptyComponent={
          <EmptyState icon="business-outline" title="Sin dependencias" message="No hay dependencias registradas" />
        }
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: colors.background },
  search: { margin: 14 },
  list: { paddingHorizontal: 14, gap: 8, paddingBottom: 20 },
  card: {
    flexDirection: 'row', alignItems: 'center', gap: 12,
    backgroundColor: colors.surface, padding: 14, borderRadius: 12,
    borderWidth: 1, borderColor: colors.border,
  },
  iconBg: {
    width: 40, height: 40, borderRadius: 10,
    backgroundColor: colors.accent + '15', justifyContent: 'center', alignItems: 'center',
  },
  info: { flex: 1 },
  name: { fontSize: 14, fontWeight: '600', color: colors.textPrimary },
  sub: { fontSize: 12, color: colors.accent, marginTop: 2 },
  address: { fontSize: 11, color: colors.textSecondary, marginTop: 2 },
});
