/**
 * RoleManagementScreen — Gestión de roles (Admin) SOJUS
 */
import React from 'react';
import { View, Text, FlatList, StyleSheet } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { colors } from '../../theme/colors';
import { PERMISSIONS, ROLES } from '../../utils/constants';
import { formatRole } from '../../utils/formatters';
import { Card } from '../../components';

export default function RoleManagementScreen() {
  const roles = Object.entries(PERMISSIONS).map(([role, permissions]) => ({
    id: role,
    name: formatRole(role),
    permissions,
  }));

  const ICONS = {
    ADMINISTRADOR: 'shield-checkmark',
    OPERADOR: 'headset',
    TECNICO: 'construct',
    GESTOR: 'stats-chart',
    AUDITOR: 'eye',
  };

  return (
    <FlatList
      style={styles.container}
      data={roles}
      keyExtractor={(item) => item.id}
      contentContainerStyle={styles.list}
      renderItem={({ item }) => (
        <View style={styles.card}>
          <View style={styles.header}>
            <View style={styles.iconBg}>
              <Ionicons name={ICONS[item.id] || 'person'} size={24} color={colors.accent} />
            </View>
            <View>
              <Text style={styles.roleName}>{item.name}</Text>
              <Text style={styles.permCount}>{item.permissions.length} permisos</Text>
            </View>
          </View>
          <View style={styles.perms}>
            {item.permissions.map((perm) => (
              <View key={perm} style={styles.permItem}>
                <Ionicons name="checkmark-circle" size={14} color={colors.success} />
                <Text style={styles.permText}>{perm}</Text>
              </View>
            ))}
          </View>
        </View>
      )}
    />
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: colors.background },
  list: { padding: 14, gap: 12 },
  card: {
    backgroundColor: colors.surface, borderRadius: 14, padding: 16,
    borderWidth: 1, borderColor: colors.border,
  },
  header: { flexDirection: 'row', alignItems: 'center', gap: 12, marginBottom: 12 },
  iconBg: {
    width: 44, height: 44, borderRadius: 12,
    backgroundColor: colors.accent + '15', justifyContent: 'center', alignItems: 'center',
  },
  roleName: { fontSize: 16, fontWeight: '700', color: colors.textPrimary },
  permCount: { fontSize: 12, color: colors.textSecondary },
  perms: { gap: 6 },
  permItem: { flexDirection: 'row', alignItems: 'center', gap: 8 },
  permText: { fontSize: 12, color: colors.textSecondary },
});
