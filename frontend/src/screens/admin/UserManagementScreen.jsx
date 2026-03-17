/**
 * UserManagementScreen — Gestión de usuarios (Admin) SOJUS
 */
import React, { useState, useCallback } from 'react';
import { View, Text, FlatList, TouchableOpacity, RefreshControl, StyleSheet } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { useFocusEffect } from '@react-navigation/native';
import { colors } from '../../theme/colors';
import { userService } from '../../api';
import { formatRole, getInitials } from '../../utils/formatters';
import { LoadingSpinner, EmptyState, SearchBar, Badge } from '../../components';

export default function UserManagementScreen({ navigation }) {
  const [users, setUsers] = useState([]);
  const [filtered, setFiltered] = useState([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);

  const load = async () => {
    try {
      const response = await userService.getAll();
      const data = Array.isArray(response.data) ? response.data : response.data.content || [];
      setUsers(data);
      setFiltered(data);
    } catch (e) { setUsers([]); setFiltered([]); }
    finally { setLoading(false); setRefreshing(false); }
  };

  useFocusEffect(useCallback(() => { load(); }, []));

  const handleSearch = (query) => {
    if (!query) { setFiltered(users); return; }
    setFiltered(users.filter((u) =>
      (u.fullName || u.username || '').toLowerCase().includes(query.toLowerCase()) ||
      (u.email || '').toLowerCase().includes(query.toLowerCase())
    ));
  };

  if (loading) return <LoadingSpinner message="Cargando usuarios..." />;

  return (
    <View style={styles.container}>
      <SearchBar placeholder="Buscar usuario..." onSearch={handleSearch} style={styles.search} />
      <FlatList
        data={filtered}
        keyExtractor={(item) => item.id.toString()}
        contentContainerStyle={styles.list}
        refreshControl={<RefreshControl refreshing={refreshing} onRefresh={() => { setRefreshing(true); load(); }} />}
        renderItem={({ item }) => (
          <View style={styles.card}>
            <View style={styles.avatar}>
              <Text style={styles.avatarText}>{getInitials(item.fullName || item.username)}</Text>
            </View>
            <View style={styles.info}>
              <Text style={styles.name}>{item.fullName || item.username}</Text>
              <Text style={styles.email}>{item.email || '—'}</Text>
            </View>
            <Badge
              label={formatRole(item.role || item.roles?.[0])}
              color={colors.accent}
              size="sm"
            />
          </View>
        )}
        ListHeaderComponent={
          <Text style={styles.count}>{filtered.length} usuario(s)</Text>
        }
        ListEmptyComponent={
          <EmptyState icon="people-outline" title="Sin usuarios" message="No hay usuarios registrados" />
        }
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: colors.background },
  search: { margin: 14 },
  list: { paddingHorizontal: 14, gap: 8, paddingBottom: 20 },
  count: { fontSize: 12, color: colors.textSecondary, marginBottom: 4 },
  card: {
    flexDirection: 'row', alignItems: 'center', gap: 12,
    backgroundColor: colors.surface, padding: 14, borderRadius: 12,
    borderWidth: 1, borderColor: colors.border,
  },
  avatar: {
    width: 40, height: 40, borderRadius: 20,
    backgroundColor: colors.primary[100], justifyContent: 'center', alignItems: 'center',
  },
  avatarText: { fontSize: 14, fontWeight: '700', color: colors.primary[700] },
  info: { flex: 1 },
  name: { fontSize: 14, fontWeight: '600', color: colors.textPrimary },
  email: { fontSize: 12, color: colors.textSecondary },
});
