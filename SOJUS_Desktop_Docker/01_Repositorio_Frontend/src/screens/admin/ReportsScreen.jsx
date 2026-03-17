/**
 * ReportsScreen — Reportes y métricas (Admin) SOJUS
 */
import React, { useState, useCallback } from 'react';
import { View, Text, ScrollView, RefreshControl, StyleSheet } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { useFocusEffect } from '@react-navigation/native';
import { colors } from '../../theme/colors';
import { ticketService } from '../../api';
import { LoadingSpinner, Card } from '../../components';

export default function ReportsScreen() {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);

  const load = async () => {
    try {
      const response = await ticketService.getStats();
      setStats(response.data);
    } catch (e) { setStats({}); }
    finally { setLoading(false); setRefreshing(false); }
  };

  useFocusEffect(useCallback(() => { load(); }, []));

  if (loading) return <LoadingSpinner message="Cargando reportes..." />;

  const metrics = [
    { label: 'Total Tickets', value: stats?.totalTickets ?? 0, icon: 'ticket', color: colors.accent },
    { label: 'Tickets Abiertos', value: stats?.ticketsAbiertos ?? 0, icon: 'lock-open', color: colors.info },
    { label: 'Tickets Cerrados', value: stats?.ticketsCerrados ?? 0, icon: 'lock-closed', color: colors.success },
    { label: 'Tiempo Promedio Resolución', value: stats?.tiempoPromedioResolucion ? `${stats.tiempoPromedioResolucion}h` : '—', icon: 'time', color: colors.warning },
    { label: 'Tickets Prioridad Alta', value: stats?.ticketsPrioridadAlta ?? 0, icon: 'alert-circle', color: colors.danger },
    { label: 'Tickets Prioridad Crítica', value: stats?.ticketsPrioridadCritica ?? 0, icon: 'flame', color: '#7C3AED' },
    { label: 'Técnicos Activos', value: stats?.tecnicosActivos ?? 0, icon: 'people', color: colors.accent },
    { label: 'Tickets Este Mes', value: stats?.ticketsMes ?? 0, icon: 'calendar', color: colors.info },
  ];

  return (
    <ScrollView
      style={styles.container}
      refreshControl={<RefreshControl refreshing={refreshing} onRefresh={() => { setRefreshing(true); load(); }} />}
    >
      <Text style={styles.title}>Métricas Generales</Text>
      <View style={styles.grid}>
        {metrics.map((m, i) => (
          <View key={i} style={styles.metricCard}>
            <View style={[styles.iconBg, { backgroundColor: m.color + '15' }]}>
              <Ionicons name={m.icon} size={20} color={m.color} />
            </View>
            <Text style={styles.metricValue}>{m.value}</Text>
            <Text style={styles.metricLabel}>{m.label}</Text>
          </View>
        ))}
      </View>
      <View style={{ height: 32 }} />
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: colors.background },
  title: {
    fontSize: 18, fontWeight: '700', color: colors.textPrimary,
    paddingHorizontal: 16, paddingTop: 16, paddingBottom: 12,
  },
  grid: { flexDirection: 'row', flexWrap: 'wrap', paddingHorizontal: 10, gap: 10 },
  metricCard: {
    backgroundColor: colors.surface, borderRadius: 14, padding: 16,
    width: '47%', flexGrow: 1, borderWidth: 1, borderColor: colors.border,
    shadowColor: '#000', shadowOffset: { width: 0, height: 1 }, shadowOpacity: 0.04, shadowRadius: 3, elevation: 2,
  },
  iconBg: {
    width: 36, height: 36, borderRadius: 10,
    justifyContent: 'center', alignItems: 'center', marginBottom: 8,
  },
  metricValue: { fontSize: 24, fontWeight: '800', color: colors.textPrimary },
  metricLabel: { fontSize: 11, color: colors.textSecondary, marginTop: 2 },
});
