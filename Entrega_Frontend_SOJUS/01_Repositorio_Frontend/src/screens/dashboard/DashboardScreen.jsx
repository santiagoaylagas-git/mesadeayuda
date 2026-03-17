/**
 * DashboardScreen — Panel principal con métricas reales SOJUS
 */
import React, { useState, useCallback } from 'react';
import {
  View, Text, StyleSheet, ScrollView,
  TouchableOpacity, RefreshControl,
} from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { useFocusEffect } from '@react-navigation/native';
import { colors } from '../../theme/colors';
import useAuth from '../../hooks/useAuth';
import usePermissions from '../../hooks/usePermissions';
import { ticketService } from '../../api';
import { LoadingSpinner } from '../../components';

export default function DashboardScreen({ navigation }) {
  const { user, logout } = useAuth();
  const { isAdmin, isOperador } = usePermissions();
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);

  const loadStats = async () => {
    try {
      const response = await ticketService.getStats();
      setStats(response.data);
    } catch (error) {
      // Fallback a stats vacías si falla
      setStats({});
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  };

  useFocusEffect(useCallback(() => { loadStats(); }, []));

  if (loading) return <LoadingSpinner message="Cargando dashboard..." />;

  const statCards = [
    { label: 'Tickets Abiertos', value: stats?.ticketsAbiertos ?? 0, icon: 'ticket-outline', color: colors.info },
    { label: 'Prioridad Alta', value: stats?.ticketsPrioridadAlta ?? 0, icon: 'alert-circle-outline', color: colors.danger },
    { label: 'Cerrados (Mes)', value: stats?.ticketsCerradosMes ?? 0, icon: 'checkmark-circle-outline', color: colors.success },
    { label: 'Hardware', value: stats?.totalHardware ?? 0, icon: 'hardware-chip-outline', color: colors.accent },
    { label: 'Software', value: stats?.totalSoftware ?? 0, icon: 'apps-outline', color: colors.warning },
    { label: 'Contratos', value: stats?.contratosVigentes ?? 0, icon: 'document-text-outline', color: colors.success },
  ];

  return (
    <ScrollView
      style={styles.container}
      refreshControl={<RefreshControl refreshing={refreshing} onRefresh={() => { setRefreshing(true); loadStats(); }} />}
    >
      {/* Welcome header */}
      <View style={styles.welcomeBar}>
        <View>
          <Text style={styles.welcomeText}>Bienvenido,</Text>
          <Text style={styles.userName}>{user?.fullName || user?.username}</Text>
          <Text style={styles.userRole}>{user?.role}</Text>
        </View>
        <TouchableOpacity style={styles.logoutBtn} onPress={logout} accessibilityLabel="Cerrar sesión">
          <Ionicons name="log-out-outline" size={22} color={colors.danger} />
        </TouchableOpacity>
      </View>

      {/* Stats grid */}
      <View style={styles.statsGrid}>
        {statCards.map((card, index) => (
          <TouchableOpacity key={index} style={styles.statCard} activeOpacity={0.7}>
            <View style={[styles.statIconBg, { backgroundColor: card.color + '15' }]}>
              <Ionicons name={card.icon} size={22} color={card.color} />
            </View>
            <Text style={styles.statValue}>{card.value}</Text>
            <Text style={styles.statLabel}>{card.label}</Text>
          </TouchableOpacity>
        ))}
      </View>

      {/* Quick Actions */}
      <Text style={styles.sectionTitle}>Acciones Rápidas</Text>
      <View style={styles.quickActions}>
        {(isAdmin || isOperador) && (
          <TouchableOpacity
            style={styles.actionBtn}
            onPress={() => navigation.navigate('TicketsTab', { screen: 'CreateTicket' })}
          >
            <Ionicons name="add-circle-outline" size={24} color={colors.accent} />
            <Text style={styles.actionBtnText}>Nuevo Ticket</Text>
          </TouchableOpacity>
        )}
        <TouchableOpacity
          style={styles.actionBtn}
          onPress={() => navigation.navigate('TicketsTab', { screen: 'TicketList' })}
        >
          <Ionicons name="list-outline" size={24} color={colors.accent} />
          <Text style={styles.actionBtnText}>Ver Tickets</Text>
        </TouchableOpacity>
        <TouchableOpacity
          style={styles.actionBtn}
          onPress={() => navigation.navigate('CatalogTab', { screen: 'ServiceCatalog' })}
        >
          <Ionicons name="book-outline" size={24} color={colors.accent} />
          <Text style={styles.actionBtnText}>Servicios</Text>
        </TouchableOpacity>
      </View>

      {/* Alert: contracts expiring */}
      {stats?.contratosProximosVencer > 0 && (
        <TouchableOpacity style={styles.alertCard}>
          <Ionicons name="warning-outline" size={22} color={colors.warning} />
          <View style={styles.alertContent}>
            <Text style={styles.alertTitle}>Contratos por Vencer</Text>
            <Text style={styles.alertText}>
              {stats.contratosProximosVencer} contrato(s) vencen en los próximos 30 días
            </Text>
          </View>
          <Ionicons name="chevron-forward" size={18} color={colors.textSecondary} />
        </TouchableOpacity>
      )}

      <View style={{ height: 32 }} />
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: colors.background },
  welcomeBar: {
    flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center',
    paddingHorizontal: 20, paddingVertical: 20,
    backgroundColor: colors.surface, borderBottomWidth: 1, borderBottomColor: colors.border,
  },
  welcomeText: { fontSize: 13, color: colors.textSecondary },
  userName: { fontSize: 20, fontWeight: '700', color: colors.textPrimary },
  userRole: {
    fontSize: 12, color: colors.accent, marginTop: 2,
    fontWeight: '500', textTransform: 'uppercase', letterSpacing: 0.5,
  },
  logoutBtn: { padding: 8, borderRadius: 8, backgroundColor: colors.dangerBg },
  statsGrid: { flexDirection: 'row', flexWrap: 'wrap', paddingHorizontal: 14, paddingTop: 16, gap: 10 },
  statCard: {
    backgroundColor: colors.surface, borderRadius: 14, padding: 16,
    width: '48%', flexGrow: 1, borderWidth: 1, borderColor: colors.border,
    shadowColor: '#000', shadowOffset: { width: 0, height: 1 }, shadowOpacity: 0.04, shadowRadius: 3, elevation: 2,
  },
  statIconBg: { width: 40, height: 40, borderRadius: 10, justifyContent: 'center', alignItems: 'center', marginBottom: 10 },
  statValue: { fontSize: 28, fontWeight: '800', color: colors.textPrimary },
  statLabel: { fontSize: 12, color: colors.textSecondary, marginTop: 2 },
  sectionTitle: {
    fontSize: 14, fontWeight: '600', color: colors.textSecondary,
    paddingHorizontal: 20, paddingTop: 24, paddingBottom: 10,
    textTransform: 'uppercase', letterSpacing: 0.5,
  },
  quickActions: { flexDirection: 'row', paddingHorizontal: 14, gap: 10 },
  actionBtn: {
    flex: 1, backgroundColor: colors.surface, borderRadius: 12,
    padding: 16, alignItems: 'center', gap: 8,
    borderWidth: 1, borderColor: colors.border,
  },
  actionBtnText: { fontSize: 12, color: colors.textPrimary, fontWeight: '500' },
  alertCard: {
    flexDirection: 'row', alignItems: 'center', backgroundColor: colors.warningBg,
    marginHorizontal: 14, marginTop: 20, padding: 16, borderRadius: 12,
    borderWidth: 1, borderColor: 'rgba(217,119,6,0.15)', gap: 12,
  },
  alertContent: { flex: 1 },
  alertTitle: { fontSize: 14, fontWeight: '600', color: colors.warning },
  alertText: { fontSize: 12, color: colors.textSecondary, marginTop: 2 },
});
