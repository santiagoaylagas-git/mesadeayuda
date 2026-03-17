/**
 * TicketForm — Formulario reutilizable para crear/editar tickets SOJUS
 */
import React, { useState, useEffect } from 'react';
import { View, Text, TouchableOpacity, StyleSheet, ScrollView } from 'react-native';
import { colors } from '../../theme/colors';
import { PRIORITIES } from '../../utils/constants';
import Input from '../common/Input';
import Button from '../common/Button';

export default function TicketForm({
  initialValues = {},
  onSubmit,
  isLoading = false,
  submitLabel = 'Crear Ticket',
  isEditing = false,
}) {
  const [asunto, setAsunto] = useState(initialValues.asunto || '');
  const [descripcion, setDescripcion] = useState(initialValues.descripcion || '');
  const [prioridad, setPrioridad] = useState(initialValues.prioridad || 'MEDIA');
  const [errors, setErrors] = useState({});

  useEffect(() => {
    if (initialValues.asunto) setAsunto(initialValues.asunto);
    if (initialValues.descripcion) setDescripcion(initialValues.descripcion);
    if (initialValues.prioridad) setPrioridad(initialValues.prioridad);
  }, [initialValues]);

  const validate = () => {
    const newErrors = {};
    if (!asunto.trim()) newErrors.asunto = 'El asunto es obligatorio';
    if (asunto.trim().length > 200) newErrors.asunto = 'Máximo 200 caracteres';
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = () => {
    if (!validate()) return;
    onSubmit({
      asunto: asunto.trim(),
      descripcion: descripcion.trim(),
      prioridad,
      canal: 'APP_MOVIL',
    });
  };

  const priorityOptions = Object.values(PRIORITIES);

  return (
    <ScrollView style={styles.container} showsVerticalScrollIndicator={false}>
      <View style={styles.form}>
        <Input
          label="Asunto"
          value={asunto}
          onChangeText={setAsunto}
          placeholder="Ej: Impresora no funciona"
          error={errors.asunto}
          required
          icon="document-text-outline"
          maxLength={200}
        />

        <Input
          label="Descripción"
          value={descripcion}
          onChangeText={setDescripcion}
          placeholder="Describa el problema en detalle..."
          multiline
          numberOfLines={5}
          icon="chatbox-outline"
        />

        <View style={styles.field}>
          <Text style={styles.label}>Prioridad</Text>
          <View style={styles.priorityButtons}>
            {priorityOptions.map((p) => {
              const color = colors.priority[p.color] || colors.textSecondary;
              const isSelected = prioridad === p.key;
              return (
                <TouchableOpacity
                  key={p.key}
                  style={[
                    styles.priorityBtn,
                    isSelected && {
                      backgroundColor: color + '15',
                      borderColor: color,
                    },
                  ]}
                  onPress={() => setPrioridad(p.key)}
                  accessibilityLabel={`Prioridad ${p.label}`}
                >
                  <View style={[styles.priorityDot, { backgroundColor: color }]} />
                  <Text
                    style={[
                      styles.priorityText,
                      isSelected && { color, fontWeight: '600' },
                    ]}
                  >
                    {p.label}
                  </Text>
                </TouchableOpacity>
              );
            })}
          </View>
        </View>

        <Button
          title={submitLabel}
          onPress={handleSubmit}
          loading={isLoading}
          icon={isEditing ? 'save-outline' : 'send'}
          fullWidth
          style={styles.submitBtn}
        />
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.background,
  },
  form: {
    padding: 20,
  },
  field: {
    marginBottom: 16,
  },
  label: {
    fontSize: 13,
    fontWeight: '600',
    color: colors.textSecondary,
    textTransform: 'uppercase',
    letterSpacing: 0.5,
    marginBottom: 6,
  },
  priorityButtons: {
    flexDirection: 'row',
    gap: 10,
  },
  priorityBtn: {
    flex: 1,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    gap: 6,
    paddingVertical: 12,
    borderRadius: 10,
    backgroundColor: colors.surface,
    borderWidth: 1,
    borderColor: colors.border,
  },
  priorityDot: {
    width: 8,
    height: 8,
    borderRadius: 4,
  },
  priorityText: {
    fontSize: 13,
    color: colors.textSecondary,
  },
  submitBtn: {
    marginTop: 10,
  },
});
