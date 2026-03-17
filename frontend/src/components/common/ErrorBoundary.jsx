/**
 * ErrorBoundary — Captura errores de renderizado y muestra fallback SOJUS
 */
import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { colors } from '../../theme/colors';
import Button from './Button';

export default class ErrorBoundary extends React.Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false, error: null };
  }

  static getDerivedStateFromError(error) {
    return { hasError: true, error };
  }

  handleReset = () => {
    this.setState({ hasError: false, error: null });
  };

  render() {
    if (this.state.hasError) {
      return (
        <View style={styles.container}>
          <Ionicons name="warning-outline" size={56} color={colors.warning} />
          <Text style={styles.title}>Algo salió mal</Text>
          <Text style={styles.message}>
            Ocurrió un error inesperado. Intentá nuevamente.
          </Text>
          <Button
            title="Reintentar"
            onPress={this.handleReset}
            variant="outline"
            icon="refresh-outline"
            style={styles.button}
          />
        </View>
      );
    }

    return this.props.children;
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 32,
    backgroundColor: colors.background,
  },
  title: {
    fontSize: 18,
    fontWeight: '700',
    color: colors.textPrimary,
    marginTop: 16,
  },
  message: {
    fontSize: 14,
    color: colors.textSecondary,
    textAlign: 'center',
    marginTop: 8,
    lineHeight: 20,
  },
  button: {
    marginTop: 24,
  },
});
