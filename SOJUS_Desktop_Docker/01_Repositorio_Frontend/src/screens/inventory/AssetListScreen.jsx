/**
 * AssetListScreen — Lista de activos (inventario) SOJUS
 */
import React, { useState, useCallback } from 'react';
import { View, Text, FlatList, TouchableOpacity, RefreshControl, StyleSheet } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { useFocusEffect } from '@react-navigation/native';
import { colors } from '../../theme/colors';
import { inventoryService } from '../../api';
import { LoadingSpinner, EmptyState, SearchBar, Badge } from '../../components';

export default function AssetListScreen({ navigation }) {
  const [tab, setTab] = useState('hardware');
  const [hardware, setHardware] = useState([]);
  const [software, setSoftware] = useState([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);

  const load = async () => {
    try {
      const [hwRes, swRes] = await Promise.all([
        inventoryService.getHardware(),
        inventoryService.getSoftware(),
      ]);
      setHardware(Array.isArray(hwRes.data) ? hwRes.data : []);
      setSoftware(Array.isArray(swRes.data) ? swRes.data : []);
    } catch (e) { /* fallback vacío */ }
    finally { setLoading(false); setRefreshing(false); }
  };

  useFocusEffect(useCallback(() => { load(); }, []));

  if (loading) return <LoadingSpinner message="Cargando inventario..." />;

  const data = tab === 'hardware' ? hardware : software;

  const renderHW = ({ item }) => (
    <TouchableOpacity
      style={styles.card}
      onPress={() => navigation.navigate('AssetDetail', { asset: item, type: 'hardware' })}
      accessibilityLabel={`${item.clase} ${item.marca} ${item.modelo}`}
    >
      <View style={styles.cardHeader}>
        <Ionicons name="hardware-chip" size={20} color={colors.accent} />
        <View style={{ flex: 1 }}>
          <Text style={styles.cardTitle}>{item.clase} — {item.marca} {item.modelo}</Text>
          <Text style={styles.cardSub}>N° Inv.: {item.inventarioPatrimonial}</Text>
        </View>
      </View>
    </TouchableOpacity>
  );

  const renderSW = ({ item }) => (
    <TouchableOpacity
      style={styles.card}
      onPress={() => navigation.navigate('AssetDetail', { asset: item, type: 'software' })}
      accessibilityLabel={item.nombre}
    >
      <View style={styles.cardHeader}>
        <Ionicons name="apps" size={20} color={colors.warning} />
        <View style={{ flex: 1 }}>
          <Text style={styles.cardTitle}>{item.nombre}</Text>
          <Text style={styles.cardSub}>{item.fabricante} — v{item.version}</Text>
        </View>
      </View>
    </TouchableOpacity>
  );

  return (
    <View style={styles.container}>
      <View style={styles.tabs}>
        <TouchableOpacity style={[styles.tab, tab === 'hardware' && styles.tabActive]} onPress={() => setTab('hardware')}>
          <Ionicons name="hardware-chip-outline" size={16} color={tab === 'hardware' ? colors.accent : colors.textSecondary} />
          <Text style={[styles.tabText, tab === 'hardware' && styles.tabTextActive]}>Hardware ({hardware.length})</Text>
        </TouchableOpacity>
        <TouchableOpacity style={[styles.tab, tab === 'software' && styles.tabActive]} onPress={() => setTab('software')}>
          <Ionicons name="apps-outline" size={16} color={tab === 'software' ? colors.accent : colors.textSecondary} />
          <Text style={[styles.tabText, tab === 'software' && styles.tabTextActive]}>Software ({software.length})</Text>
        </TouchableOpacity>
      </View>
      <FlatList
        data={data}
        keyExtractor={(item) => item.id.toString()}
        renderItem={tab === 'hardware' ? renderHW : renderSW}
        contentContainerStyle={styles.list}
        refreshControl={<RefreshControl refreshing={refreshing} onRefresh={() => { setRefreshing(true); load(); }} />}
        ListEmptyComponent={
          <EmptyState icon={tab === 'hardware' ? 'hardware-chip-outline' : 'apps-outline'} title={`Sin ${tab}`} message={`No hay ${tab} registrado`} />
        }
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: colors.background },
  tabs: { flexDirection: 'row', backgroundColor: colors.surface, borderBottomWidth: 1, borderBottomColor: colors.border },
  tab: { flex: 1, flexDirection: 'row', alignItems: 'center', justifyContent: 'center', gap: 6, paddingVertical: 14 },
  tabActive: { borderBottomWidth: 2, borderBottomColor: colors.accent },
  tabText: { fontSize: 13, color: colors.textSecondary, fontWeight: '500' },
  tabTextActive: { color: colors.accent, fontWeight: '600' },
  list: { padding: 14, gap: 10 },
  card: {
    backgroundColor: colors.surface, borderRadius: 12, padding: 16,
    borderWidth: 1, borderColor: colors.border,
  },
  cardHeader: { flexDirection: 'row', alignItems: 'center', gap: 12 },
  cardTitle: { fontSize: 14, fontWeight: '600', color: colors.textPrimary },
  cardSub: { fontSize: 12, color: colors.textSecondary, marginTop: 2 },
});
