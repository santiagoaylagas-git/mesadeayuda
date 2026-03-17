/**
 * AssetDetailScreen — Detalle de activo SOJUS
 */
import React from 'react';
import { View, Text, ScrollView, StyleSheet } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { colors } from '../../theme/colors';
import { Card } from '../../components';

export default function AssetDetailScreen({ route }) {
  const { asset, type } = route.params;
  const isHW = type === 'hardware';

  const fields = isHW
    ? [
        { label: 'Clase', value: asset.clase },
        { label: 'Tipo', value: asset.tipo },
        { label: 'Marca', value: asset.marca },
        { label: 'Modelo', value: asset.modelo },
        { label: 'N° Inventario', value: asset.inventarioPatrimonial },
        { label: 'N° Serie', value: asset.numeroSerie },
        { label: 'Estado', value: asset.estado },
        { label: 'Ubicación', value: asset.ubicacionFisica },
      ]
    : [
        { label: 'Nombre', value: asset.nombre },
        { label: 'Fabricante', value: asset.fabricante },
        { label: 'Versión', value: asset.version },
        { label: 'Tipo Licencia', value: asset.tipoLicencia },
        { label: 'Cantidad Licencias', value: asset.cantidadLicencias },
        { label: 'Estado', value: asset.estado },
        { label: 'Fecha Vencimiento', value: asset.fechaVencimiento },
      ];

  return (
    <ScrollView style={styles.container}>
      <View style={styles.header}>
        <Ionicons
          name={isHW ? 'hardware-chip' : 'apps'}
          size={32}
          color={isHW ? colors.accent : colors.warning}
        />
        <Text style={styles.title}>
          {isHW ? `${asset.marca} ${asset.modelo}` : asset.nombre}
        </Text>
        <Text style={styles.subtitle}>
          {isHW ? asset.inventarioPatrimonial : `v${asset.version}`}
        </Text>
      </View>

      <View style={styles.fields}>
        {fields.map((f, i) => (
          <View key={i} style={styles.fieldRow}>
            <Text style={styles.fieldLabel}>{f.label}</Text>
            <Text style={styles.fieldValue}>{f.value || '—'}</Text>
          </View>
        ))}
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: colors.background },
  header: {
    backgroundColor: colors.surface, padding: 24, alignItems: 'center',
    borderBottomWidth: 1, borderBottomColor: colors.border, gap: 8,
  },
  title: { fontSize: 18, fontWeight: '700', color: colors.textPrimary },
  subtitle: { fontSize: 13, color: colors.textSecondary },
  fields: {
    backgroundColor: colors.surface, margin: 14, borderRadius: 12,
    borderWidth: 1, borderColor: colors.border, overflow: 'hidden',
  },
  fieldRow: {
    flexDirection: 'row', justifyContent: 'space-between',
    paddingHorizontal: 16, paddingVertical: 12,
    borderBottomWidth: 1, borderBottomColor: colors.border,
  },
  fieldLabel: { fontSize: 13, color: colors.textSecondary },
  fieldValue: { fontSize: 13, fontWeight: '600', color: colors.textPrimary },
});
