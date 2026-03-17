/**
 * TicketCard — Tarjeta de ticket para listas SOJUS
 */
import React from 'react';
import { View, Text, TouchableOpacity, StyleSheet, Platform } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { colors } from '../../theme/colors';
import { TICKET_STATUS, PRIORITIES } from '../../utils/constants';
import { formatDate, truncate } from '../../utils/formatters';
import Badge from '../common/Badge';

export default function TicketCard({ ticket, onPress }) {
  const statusInfo = TICKET_STATUS[ticket.status] || {};
  const statusColors = colors.status[statusInfo.color] || {};
  const priorityInfo = PRIORITIES[ticket.prioridad] || {};
  const priorityColor = colors.priority[priorityInfo.color] || colors.textSecondary;

  return (
    <TouchableOpacity
      style={styles.card}
      onPress={() => onPress?.(ticket)}
      activeOpacity={0.7}
      accessibilityLabel={`Ticket ${ticket.id}: ${ticket.asunto}`}
    >
      <View style={styles.header}>
        <Text style={styles.ticketId}>#{ticket.id}</Text>
        <Badge
          label={statusInfo.label || ticket.status}
          color={statusColors.text}
          bgColor={statusColors.bg}
          borderColor={statusColors.border}
          size="sm"
        />
      </View>

      <Text style={styles.subject} numberOfLines={2}>{ticket.asunto}</Text>

      <View style={styles.meta}>
        <View style={[styles.priorityDot, { backgroundColor: priorityColor }]} />
        <Text style={styles.metaText}>{priorityInfo.label || ticket.prioridad}</Text>
        {ticket.juzgadoNombre && (
          <>
            <Text style={styles.separator}>·</Text>
            <Ionicons name="location-outline" size={12} color={colors.textSecondary} />
            <Text style={styles.metaText} numberOfLines={1}>
              {truncate(ticket.juzgadoNombre, 25)}
            </Text>
          </>
        )}
      </View>

      <View style={styles.footer}>
        {ticket.tecnicoNombre ? (
          <View style={styles.footerItem}>
            <Ionicons name="person-outline" size={12} color={colors.textSecondary} />
            <Text style={styles.footerText}>{ticket.tecnicoNombre}</Text>
          </View>
        ) : (
          <View style={styles.footerItem}>
            <Ionicons name="person-outline" size={12} color={colors.textDisabled} />
            <Text style={[styles.footerText, { color: colors.textDisabled }]}>Sin asignar</Text>
          </View>
        )}
        <Text style={styles.footerDate}>{formatDate(ticket.createdAt)}</Text>
      </View>
    </TouchableOpacity>
  );
}

const styles = StyleSheet.create({
  card: {
    backgroundColor: colors.surface,
    borderRadius: 12,
    padding: 16,
    borderWidth: 1,
    borderColor: colors.border,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.04,
    shadowRadius: 3,
    elevation: 2,
  },
  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 8,
  },
  ticketId: {
    fontSize: 12,
    color: colors.textSecondary,
    fontWeight: '600',
    fontFamily: Platform?.OS === 'ios' ? 'Menlo' : 'monospace',
  },
  subject: {
    fontSize: 15,
    fontWeight: '600',
    color: colors.textPrimary,
    marginBottom: 8,
  },
  meta: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 6,
    marginBottom: 10,
  },
  priorityDot: {
    width: 8,
    height: 8,
    borderRadius: 4,
  },
  metaText: {
    fontSize: 12,
    color: colors.textSecondary,
  },
  separator: {
    color: colors.border,
  },
  footer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    borderTopWidth: 1,
    borderTopColor: colors.border,
    paddingTop: 8,
  },
  footerItem: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 4,
  },
  footerText: {
    fontSize: 11,
    color: colors.textSecondary,
  },
  footerDate: {
    fontSize: 11,
    color: colors.textDisabled,
  },
});
