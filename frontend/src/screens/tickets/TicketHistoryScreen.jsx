/**
 * TicketHistoryScreen — Historial completo de un ticket SOJUS
 */
import React, { useState, useEffect } from 'react';
import { View, ScrollView, StyleSheet } from 'react-native';
import { colors } from '../../theme/colors';
import { ticketService } from '../../api';
import { TicketTimeline, LoadingSpinner } from '../../components';

export default function TicketHistoryScreen({ route }) {
  const { ticketId } = route.params;
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadHistory();
  }, [ticketId]);

  const loadHistory = async () => {
    try {
      const response = await ticketService.getHistory(ticketId);
      setHistory(response.data || []);
    } catch (error) {
      setHistory([]);
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <LoadingSpinner message="Cargando historial..." />;

  return (
    <ScrollView style={styles.container} contentContainerStyle={styles.content}>
      <TicketTimeline events={history} />
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: colors.background },
  content: { padding: 20 },
});
