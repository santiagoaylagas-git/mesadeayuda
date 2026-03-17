/**
 * ServiceCatalogScreen — Catálogo de servicios disponibles SOJUS
 */
import React, { useState, useCallback } from 'react';
import { View, Text, FlatList, TouchableOpacity, RefreshControl, StyleSheet } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { useFocusEffect } from '@react-navigation/native';
import { colors } from '../../theme/colors';
import { catalogService } from '../../api';
import { LoadingSpinner, EmptyState, Card } from '../../components';

const QUICK_SERVICES = [
  { id: 'internet', icon: 'wifi-outline', label: 'Sin Internet', color: colors.danger },
  { id: 'impresora', icon: 'print-outline', label: 'Impresora', color: colors.warning },
  { id: 'toner', icon: 'color-fill-outline', label: 'Solicitar Tóner', color: colors.info },
  { id: 'email', icon: 'mail-outline', label: 'Email', color: colors.accent },
  { id: 'software', icon: 'apps-outline', label: 'Instalar Software', color: colors.success },
  { id: 'otro', icon: 'help-circle-outline', label: 'Otro', color: colors.textSecondary },
];

export default function ServiceCatalogScreen({ navigation }) {
  const [services, setServices] = useState([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);

  const loadServices = async () => {
    try {
      const response = await catalogService.getServices();
      setServices(Array.isArray(response.data) ? response.data : []);
    } catch (e) {
      setServices([]);
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  };

  useFocusEffect(useCallback(() => { loadServices(); }, []));

  if (loading) return <LoadingSpinner message="Cargando servicios..." />;

  return (
    <View style={styles.container}>
      <FlatList
        data={services.length > 0 ? services : QUICK_SERVICES}
        keyExtractor={(item) => item.id.toString()}
        numColumns={2}
        columnWrapperStyle={styles.row}
        contentContainerStyle={styles.list}
        refreshControl={<RefreshControl refreshing={refreshing} onRefresh={() => { setRefreshing(true); loadServices(); }} />}
        ListHeaderComponent={
          <View>
            <Text style={styles.sectionTitle}>Servicios Rápidos</Text>
            <Text style={styles.subtitle}>Seleccioná el tipo de incidente para crear un ticket</Text>
          </View>
        }
        renderItem={({ item }) => (
          <TouchableOpacity
            style={styles.serviceCard}
            onPress={() => navigation.navigate('ServiceRequest', {
              serviceId: item.id,
              serviceName: item.label || item.nombre,
            })}
            accessibilityLabel={item.label || item.nombre}
          >
            <View style={[styles.iconBg, { backgroundColor: (item.color || colors.accent) + '15' }]}>
              <Ionicons name={item.icon || 'cube-outline'} size={28} color={item.color || colors.accent} />
            </View>
            <Text style={styles.serviceLabel}>{item.label || item.nombre}</Text>
          </TouchableOpacity>
        )}
        ListFooterComponent={
          <View style={styles.footer}>
            <TouchableOpacity
              style={styles.faqBtn}
              onPress={() => navigation.navigate('FAQ')}
            >
              <Ionicons name="help-buoy-outline" size={20} color={colors.accent} />
              <Text style={styles.faqBtnText}>Ver Preguntas Frecuentes (FAQ)</Text>
              <Ionicons name="chevron-forward" size={18} color={colors.textSecondary} />
            </TouchableOpacity>
          </View>
        }
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: colors.background },
  list: { padding: 14 },
  row: { gap: 10, marginBottom: 10 },
  sectionTitle: { fontSize: 18, fontWeight: '700', color: colors.textPrimary, marginBottom: 4 },
  subtitle: { fontSize: 13, color: colors.textSecondary, marginBottom: 16 },
  serviceCard: {
    flex: 1, backgroundColor: colors.surface, borderRadius: 14,
    padding: 20, alignItems: 'center', gap: 10,
    borderWidth: 1, borderColor: colors.border,
  },
  iconBg: {
    width: 56, height: 56, borderRadius: 14,
    justifyContent: 'center', alignItems: 'center',
  },
  serviceLabel: { fontSize: 13, fontWeight: '600', color: colors.textPrimary, textAlign: 'center' },
  footer: { marginTop: 16 },
  faqBtn: {
    flexDirection: 'row', alignItems: 'center', gap: 10,
    backgroundColor: colors.surface, padding: 16, borderRadius: 12,
    borderWidth: 1, borderColor: colors.border,
  },
  faqBtnText: { flex: 1, fontSize: 14, color: colors.textPrimary, fontWeight: '500' },
});
