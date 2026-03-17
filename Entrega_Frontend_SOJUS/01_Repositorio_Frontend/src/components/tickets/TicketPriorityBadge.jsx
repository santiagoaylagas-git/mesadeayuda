/**
 * TicketPriorityBadge — Badge de prioridad de ticket SOJUS
 */
import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { colors } from '../../theme/colors';
import { PRIORITIES } from '../../utils/constants';

export default function TicketPriorityBadge({ priority, size = 'md' }) {
  const info = PRIORITIES[priority] || {};
  const color = colors.priority[info.color] || colors.textSecondary;

  return (
    <View style={[styles.container, size === 'sm' && styles.sm]} accessibilityLabel={`Prioridad: ${info.label || priority}`}>
      <View style={[styles.dot, { backgroundColor: color }]} />
      <Text style={[styles.label, { color }, size === 'sm' && styles.labelSm]}>
        {info.label || priority}
      </Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 6,
  },
  sm: {
    gap: 4,
  },
  dot: {
    width: 8,
    height: 8,
    borderRadius: 4,
  },
  label: {
    fontSize: 13,
    fontWeight: '600',
  },
  labelSm: {
    fontSize: 11,
  },
});
