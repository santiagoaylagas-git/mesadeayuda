/**
 * TicketDetailScreen — Detalle completo de ticket SOJUS
 */
import React, { useState, useEffect } from 'react';
import {
  View, Text, StyleSheet, ScrollView, Alert, TouchableOpacity,
} from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { colors } from '../../theme/colors';
import { TICKET_STATUS, STATUS_TRANSITIONS } from '../../utils/constants';
import { formatDate } from '../../utils/formatters';
import { ticketService } from '../../api';
import { getErrorMessage } from '../../utils/errorHandler';
import usePermissions from '../../hooks/usePermissions';
import { TicketStatusBadge, TicketPriorityBadge, TicketTimeline, LoadingSpinner, Button, Modal } from '../../components';
import Toast from 'react-native-toast-message';

export default function TicketDetailScreen({ route, navigation }) {
  const { ticketId } = route.params;
  const { hasPermission, isAdmin } = usePermissions();
  const [ticket, setTicket] = useState(null);
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true);
  const [confirmModal, setConfirmModal] = useState(null);

  useEffect(() => { loadTicket(); }, [ticketId]);

  const loadTicket = async () => {
    try {
      const [ticketRes, historyRes] = await Promise.all([
        ticketService.getById(ticketId),
        ticketService.getHistory(ticketId).catch(() => ({ data: [] })),
      ]);
      setTicket(ticketRes.data);
      setHistory(historyRes.data || []);
    } catch (error) {
      Alert.alert('Error', getErrorMessage(error));
      navigation.goBack();
    } finally {
      setLoading(false);
    }
  };

  const handleStatusChange = (newStatus) => {
    const statusLabel = TICKET_STATUS[newStatus]?.label || newStatus;
    setConfirmModal({
      title: 'Cambiar Estado',
      message: `¿Cambiar el estado del ticket a "${statusLabel}"?`,
      onConfirm: async () => {
        setConfirmModal(null);
        try {
          await ticketService.changeStatus(ticketId, {
            status: newStatus,
            comentario: `Estado cambiado a ${statusLabel}`,
          });
          Toast.show({ type: 'success', text1: 'Estado actualizado', text2: `Ticket cambiado a ${statusLabel}` });
          loadTicket();
        } catch (error) {
          Toast.show({ type: 'error', text1: 'Error', text2: getErrorMessage(error) });
        }
      },
    });
  };

  if (loading) return <LoadingSpinner message="Cargando ticket..." />;
  if (!ticket) return null;

  const availableTransitions = STATUS_TRANSITIONS[ticket.status] || [];

  return (
    <ScrollView style={styles.container}>
      {/* Header */}
      <View style={styles.headerSection}>
        <View style={styles.headerRow}>
          <Text style={styles.ticketIdLabel}>Ticket #{ticket.id}</Text>
          <TicketStatusBadge status={ticket.status} />
        </View>
        <Text style={styles.subject}>{ticket.asunto}</Text>
      </View>

      {/* Info Grid */}
      <View style={styles.infoGrid}>
        <InfoItem icon="alert-circle" label="Prioridad">
          <TicketPriorityBadge priority={ticket.prioridad} size="sm" />
        </InfoItem>
        <InfoItem icon="location" label="Juzgado" value={ticket.juzgadoNombre || '—'} />
        <InfoItem icon="person" label="Solicitante" value={ticket.solicitanteNombre || '—'} />
        <InfoItem icon="construct" label="Técnico" value={ticket.tecnicoNombre || 'Sin asignar'} />
        <InfoItem icon="hardware-chip" label="Equipo" value={ticket.hardwareInventario || '—'} />
        <InfoItem icon="radio" label="Canal" value={ticket.canal || '—'} />
      </View>

      {/* Descripción */}
      {ticket.descripcion && (
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>Descripción</Text>
          <Text style={styles.description}>{ticket.descripcion}</Text>
        </View>
      )}

      {/* Bitácora */}
      {ticket.bitacora && (
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>Bitácora</Text>
          <View style={styles.logBox}>
            <Text style={styles.logText}>{ticket.bitacora}</Text>
          </View>
        </View>
      )}

      {/* Fechas */}
      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Fechas</Text>
        <DateRow label="Creado" value={formatDate(ticket.createdAt)} />
        {ticket.updatedAt && <DateRow label="Actualizado" value={formatDate(ticket.updatedAt)} />}
        {ticket.closedAt && <DateRow label="Cerrado" value={formatDate(ticket.closedAt)} />}
      </View>

      {/* Timeline */}
      {history.length > 0 && (
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>Historial</Text>
          <TicketTimeline events={history} />
        </View>
      )}

      {/* Actions */}
      {availableTransitions.length > 0 && hasPermission('tickets.update') && (
        <View style={styles.actionsSection}>
          <Text style={styles.sectionTitle}>Acciones</Text>
          <View style={styles.actionBtns}>
            {availableTransitions.map((status) => (
              <Button
                key={status}
                title={TICKET_STATUS[status]?.label || status}
                onPress={() => handleStatusChange(status)}
                variant={status === 'CERRADO' ? 'danger' : 'secondary'}
                size="sm"
                style={{ flex: 1 }}
              />
            ))}
          </View>
          {(isAdmin || hasPermission('tickets.assign')) && ticket.status !== 'CERRADO' && (
            <Button
              title="Asignar Técnico"
              onPress={() => navigation.navigate('AssignTechnician', { ticketId: ticket.id })}
              variant="outline"
              icon="person-add-outline"
              fullWidth
              style={{ marginTop: 10 }}
            />
          )}
          {hasPermission('tickets.update') && ticket.status !== 'CERRADO' && (
            <Button
              title="Editar Ticket"
              onPress={() => navigation.navigate('EditTicket', { ticket })}
              variant="ghost"
              icon="create-outline"
              fullWidth
              style={{ marginTop: 6 }}
            />
          )}
        </View>
      )}

      <View style={{ height: 40 }} />

      {/* Confirm Modal */}
      {confirmModal && (
        <Modal
          visible={!!confirmModal}
          title={confirmModal.title}
          message={confirmModal.message}
          onConfirm={confirmModal.onConfirm}
          onCancel={() => setConfirmModal(null)}
          confirmVariant="primary"
          confirmText="Confirmar"
        />
      )}
    </ScrollView>
  );
}

function InfoItem({ icon, label, value, children }) {
  return (
    <View style={styles.infoItem}>
      <Ionicons name={icon} size={16} color={colors.textSecondary} />
      <View>
        <Text style={styles.infoLabel}>{label}</Text>
        {children || <Text style={styles.infoValue}>{value}</Text>}
      </View>
    </View>
  );
}

function DateRow({ label, value }) {
  return (
    <View style={styles.dateRow}>
      <Text style={styles.dateLabel}>{label}:</Text>
      <Text style={styles.dateValue}>{value}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: colors.background },
  headerSection: {
    backgroundColor: colors.surface, padding: 20,
    borderBottomWidth: 1, borderBottomColor: colors.border,
  },
  headerRow: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', marginBottom: 8 },
  ticketIdLabel: { fontSize: 13, color: colors.textSecondary, fontWeight: '600' },
  subject: { fontSize: 18, fontWeight: '700', color: colors.textPrimary },
  infoGrid: { flexDirection: 'row', flexWrap: 'wrap', padding: 14, gap: 8 },
  infoItem: {
    flexDirection: 'row', alignItems: 'center', gap: 8,
    backgroundColor: colors.surface, paddingHorizontal: 12, paddingVertical: 10,
    borderRadius: 10, borderWidth: 1, borderColor: colors.border,
    width: '48%', flexGrow: 1,
  },
  infoLabel: { fontSize: 10, color: colors.textDisabled, textTransform: 'uppercase' },
  infoValue: { fontSize: 13, fontWeight: '600', color: colors.textPrimary },
  section: { paddingHorizontal: 14, paddingTop: 16 },
  sectionTitle: {
    fontSize: 13, fontWeight: '600', color: colors.textSecondary,
    textTransform: 'uppercase', letterSpacing: 0.5, marginBottom: 8,
  },
  description: {
    fontSize: 14, color: colors.textPrimary, lineHeight: 22,
    backgroundColor: colors.surface, padding: 14, borderRadius: 10,
    borderWidth: 1, borderColor: colors.border,
  },
  logBox: { backgroundColor: '#0d1117', padding: 14, borderRadius: 10 },
  logText: { fontSize: 12, color: '#3fb950', fontFamily: 'monospace', lineHeight: 20 },
  dateRow: {
    flexDirection: 'row', justifyContent: 'space-between',
    paddingVertical: 6, borderBottomWidth: 1, borderBottomColor: colors.border,
  },
  dateLabel: { fontSize: 13, color: colors.textSecondary },
  dateValue: { fontSize: 13, color: colors.textPrimary, fontWeight: '500' },
  actionsSection: { paddingHorizontal: 14, paddingTop: 20 },
  actionBtns: { flexDirection: 'row', gap: 10 },
});
