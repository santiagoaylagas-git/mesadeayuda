/**
 * TicketTimeline — Historial visual de cambios de un ticket SOJUS
 */
import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { colors } from '../../theme/colors';
import { formatDate } from '../../utils/formatters';

export default function TicketTimeline({ events = [] }) {
  if (!events.length) {
    return (
      <View style={styles.empty}>
        <Text style={styles.emptyText}>Sin historial registrado</Text>
      </View>
    );
  }

  return (
    <View style={styles.container}>
      {events.map((event, index) => (
        <View key={index} style={styles.item}>
          <View style={styles.lineContainer}>
            <View style={[styles.dot, index === 0 && styles.dotActive]} />
            {index < events.length - 1 && <View style={styles.line} />}
          </View>
          <View style={styles.content}>
            <Text style={styles.action}>{event.descripcion || event.action}</Text>
            <View style={styles.metaRow}>
              {event.usuario && (
                <View style={styles.metaItem}>
                  <Ionicons name="person-outline" size={11} color={colors.textSecondary} />
                  <Text style={styles.metaText}>{event.usuario}</Text>
                </View>
              )}
              <Text style={styles.date}>{formatDate(event.fecha || event.createdAt)}</Text>
            </View>
            {event.comentario && (
              <Text style={styles.comment}>{event.comentario}</Text>
            )}
          </View>
        </View>
      ))}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    paddingLeft: 4,
  },
  empty: {
    padding: 20,
    alignItems: 'center',
  },
  emptyText: {
    fontSize: 13,
    color: colors.textSecondary,
  },
  item: {
    flexDirection: 'row',
    marginBottom: 4,
  },
  lineContainer: {
    alignItems: 'center',
    width: 24,
  },
  dot: {
    width: 10,
    height: 10,
    borderRadius: 5,
    backgroundColor: colors.border,
    marginTop: 4,
  },
  dotActive: {
    backgroundColor: colors.accent,
  },
  line: {
    width: 2,
    flex: 1,
    backgroundColor: colors.border,
  },
  content: {
    flex: 1,
    paddingBottom: 16,
    paddingLeft: 10,
  },
  action: {
    fontSize: 13,
    fontWeight: '600',
    color: colors.textPrimary,
  },
  metaRow: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 8,
    marginTop: 4,
  },
  metaItem: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 4,
  },
  metaText: {
    fontSize: 11,
    color: colors.textSecondary,
  },
  date: {
    fontSize: 11,
    color: colors.textDisabled,
  },
  comment: {
    fontSize: 12,
    color: colors.textSecondary,
    marginTop: 4,
    fontStyle: 'italic',
    backgroundColor: colors.background,
    padding: 8,
    borderRadius: 6,
  },
});
