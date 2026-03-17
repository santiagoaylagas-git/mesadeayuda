/**
 * FAQScreen — Preguntas frecuentes SOJUS
 */
import React, { useState, useCallback } from 'react';
import { View, Text, TouchableOpacity, FlatList, RefreshControl, StyleSheet } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { useFocusEffect } from '@react-navigation/native';
import { colors } from '../../theme/colors';
import { catalogService } from '../../api';
import { LoadingSpinner, EmptyState, SearchBar } from '../../components';

const DEFAULT_FAQS = [
  { id: 1, question: '¿Cómo cambio mi contraseña?', answer: 'Desde el menú de perfil, seleccioná "Cambiar contraseña" e ingresá tu contraseña actual y la nueva.' },
  { id: 2, question: '¿Cómo creo un ticket de soporte?', answer: 'Tocá el botón "+" en la lista de tickets, completá el formulario con asunto y descripción, y seleccioná la prioridad.' },
  { id: 3, question: '¿Cuánto tardan en resolver un ticket?', answer: 'El tiempo de resolución depende de la prioridad: Alta (4 horas), Media (24 horas), Baja (48 horas).' },
  { id: 4, question: '¿Cómo verifico el estado de mi ticket?', answer: 'Ingresá a "Mis Tickets" en la pestaña de Tickets para ver el estado actualizado de tus solicitudes.' },
  { id: 5, question: 'Mi impresora no imprime, ¿qué hago?', answer: '1. Verificá que esté encendida y conectada. 2. Reiniciá la impresora. 3. Si el problema persiste, creá un ticket de soporte.' },
  { id: 6, question: 'No tengo internet, ¿qué hago?', answer: '1. Verificá el cable de red. 2. Reiniciá tu equipo. 3. Si el problema persiste, contactá a la Mesa de Ayuda.' },
];

export default function FAQScreen() {
  const [faqs, setFaqs] = useState([]);
  const [filtered, setFiltered] = useState([]);
  const [loading, setLoading] = useState(true);
  const [expanded, setExpanded] = useState(null);

  const load = async () => {
    try {
      const response = await catalogService.getFAQ();
      const data = Array.isArray(response.data) ? response.data : DEFAULT_FAQS;
      setFaqs(data);
      setFiltered(data);
    } catch (e) {
      setFaqs(DEFAULT_FAQS);
      setFiltered(DEFAULT_FAQS);
    } finally {
      setLoading(false);
    }
  };

  useFocusEffect(useCallback(() => { load(); }, []));

  const handleSearch = (query) => {
    if (!query) {
      setFiltered(faqs);
    } else {
      setFiltered(faqs.filter((f) =>
        (f.question || f.pregunta || '').toLowerCase().includes(query.toLowerCase()) ||
        (f.answer || f.respuesta || '').toLowerCase().includes(query.toLowerCase())
      ));
    }
  };

  if (loading) return <LoadingSpinner message="Cargando preguntas..." />;

  return (
    <View style={styles.container}>
      <SearchBar placeholder="Buscar en FAQ..." onSearch={handleSearch} style={styles.search} />
      <FlatList
        data={filtered}
        keyExtractor={(item) => item.id.toString()}
        contentContainerStyle={styles.list}
        renderItem={({ item }) => (
          <TouchableOpacity
            style={styles.card}
            onPress={() => setExpanded(expanded === item.id ? null : item.id)}
            accessibilityLabel={item.question || item.pregunta}
          >
            <View style={styles.questionRow}>
              <Ionicons name="help-circle-outline" size={20} color={colors.accent} />
              <Text style={styles.question}>{item.question || item.pregunta}</Text>
              <Ionicons
                name={expanded === item.id ? 'chevron-up' : 'chevron-down'}
                size={18} color={colors.textSecondary}
              />
            </View>
            {expanded === item.id && (
              <Text style={styles.answer}>{item.answer || item.respuesta}</Text>
            )}
          </TouchableOpacity>
        )}
        ListEmptyComponent={
          <EmptyState icon="help-buoy-outline" title="Sin resultados" message="No se encontraron preguntas" />
        }
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: colors.background },
  search: { margin: 14 },
  list: { paddingHorizontal: 14, gap: 8, paddingBottom: 20 },
  card: {
    backgroundColor: colors.surface, borderRadius: 12, padding: 16,
    borderWidth: 1, borderColor: colors.border,
  },
  questionRow: { flexDirection: 'row', alignItems: 'center', gap: 10 },
  question: { flex: 1, fontSize: 14, fontWeight: '600', color: colors.textPrimary },
  answer: {
    fontSize: 13, color: colors.textSecondary, lineHeight: 20,
    marginTop: 12, paddingTop: 12, borderTopWidth: 1, borderTopColor: colors.border,
  },
});
