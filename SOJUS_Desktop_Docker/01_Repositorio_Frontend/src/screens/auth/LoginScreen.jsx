/**
 * LoginScreen — Pantalla de inicio de sesión SOJUS
 */
import React, { useState } from 'react';
import {
  View, Text, TouchableOpacity, StyleSheet,
  KeyboardAvoidingView, Platform, Alert,
} from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { colors } from '../../theme/colors';
import useAuth from '../../hooks/useAuth';
import Input from '../../components/common/Input';
import Button from '../../components/common/Button';

export default function LoginScreen({ navigation }) {
  const { login } = useAuth();
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleLogin = async () => {
    if (!username.trim() || !password.trim()) {
      setError('Ingresá usuario y contraseña');
      return;
    }
    setError('');
    setLoading(true);
    try {
      const result = await login(username.trim(), password);
      if (!result.success) {
        setError(result.error);
      }
    } catch (e) {
      setError('Error de conexión. Verificá tu red.');
    } finally {
      setLoading(false);
    }
  };

  // Acceso rápido demo
  const quickLogin = async (user, pass) => {
    setUsername(user);
    setPassword(pass);
    setError('');
    setLoading(true);
    try {
      const result = await login(user, pass);
      if (!result.success) {
        setError(result.error);
      }
    } catch (e) {
      setError('Error de conexión');
    } finally {
      setLoading(false);
    }
  };

  return (
    <KeyboardAvoidingView
      style={styles.container}
      behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
    >
      <View style={styles.bgAccent} />
      <View style={styles.content}>
        {/* Brand */}
        <View style={styles.brandSection}>
          <Ionicons name="shield-checkmark" size={56} color={colors.accentLight} />
          <Text style={styles.brandName}>SOJUS</Text>
          <Text style={styles.brandSub}>Sistema de Gestión Judicial</Text>
          <Text style={styles.brandOrg}>Poder Judicial · Provincia de Santa Fe</Text>
        </View>

        {/* Login Form */}
        <View style={styles.formCard}>
          <Input
            value={username}
            onChangeText={(v) => { setUsername(v); setError(''); }}
            placeholder="Usuario"
            icon="person-outline"
            autoCapitalize="none"
            autoCorrect={false}
            style={styles.darkInput}
            inputStyle={styles.darkInputText}
          />
          <Input
            value={password}
            onChangeText={(v) => { setPassword(v); setError(''); }}
            placeholder="Contraseña"
            icon="lock-closed-outline"
            secureTextEntry
            style={styles.darkInput}
            inputStyle={styles.darkInputText}
          />

          {error ? (
            <View style={styles.errorBox}>
              <Ionicons name="alert-circle" size={16} color={colors.danger} />
              <Text style={styles.errorText}>{error}</Text>
            </View>
          ) : null}

          <Button
            title="Iniciar Sesión"
            onPress={handleLogin}
            loading={loading}
            fullWidth
            style={styles.loginBtn}
          />
        </View>

        {/* Quick login demo */}
        <View style={styles.demoSection}>
          <Text style={styles.demoTitle}>Acceso Rápido (Demo)</Text>
          <View style={styles.demoButtons}>
            {[
              { label: 'Admin', user: 'admin', pass: 'admin123', icon: 'shield' },
              { label: 'Operador', user: 'operador', pass: 'oper123', icon: 'headset' },
              { label: 'Técnico', user: 'tecnico', pass: 'tec123', icon: 'construct' },
            ].map((item) => (
              <TouchableOpacity
                key={item.user}
                style={styles.demoBtn}
                onPress={() => quickLogin(item.user, item.pass)}
                disabled={loading}
                accessibilityLabel={`Login rápido como ${item.label}`}
              >
                <Ionicons name={item.icon} size={18} color={colors.accentLight} />
                <Text style={styles.demoBtnText}>{item.label}</Text>
              </TouchableOpacity>
            ))}
          </View>
        </View>
      </View>
    </KeyboardAvoidingView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: colors.primary[900] },
  bgAccent: {
    position: 'absolute', top: -100, right: -100,
    width: 300, height: 300, borderRadius: 150,
    backgroundColor: 'rgba(3,105,161,0.06)',
  },
  content: { flex: 1, justifyContent: 'center', paddingHorizontal: 32 },
  brandSection: { alignItems: 'center', marginBottom: 40 },
  brandName: {
    fontSize: 36, fontWeight: '800', color: '#fff',
    letterSpacing: 4, marginTop: 12,
  },
  brandSub: { fontSize: 14, color: colors.accentLight, marginTop: 4, letterSpacing: 1 },
  brandOrg: { fontSize: 11, color: 'rgba(255,255,255,0.5)', marginTop: 6 },
  formCard: {
    backgroundColor: 'rgba(255,255,255,0.04)',
    borderRadius: 16, padding: 24,
    borderWidth: 1, borderColor: 'rgba(255,255,255,0.06)',
  },
  darkInput: {
    marginBottom: 8,
  },
  darkInputText: {
    color: '#fff',
  },
  errorBox: {
    flexDirection: 'row', alignItems: 'center', gap: 6,
    backgroundColor: colors.dangerBg, padding: 10,
    borderRadius: 8, marginBottom: 12,
  },
  errorText: { color: colors.danger, fontSize: 13, flex: 1 },
  loginBtn: { marginTop: 4, backgroundColor: colors.accent, borderColor: colors.accent },
  demoSection: { marginTop: 32, alignItems: 'center' },
  demoTitle: {
    color: 'rgba(255,255,255,0.4)', fontSize: 11,
    textTransform: 'uppercase', letterSpacing: 1.5, marginBottom: 12,
  },
  demoButtons: { flexDirection: 'row', gap: 12 },
  demoBtn: {
    flexDirection: 'row', alignItems: 'center', gap: 6,
    backgroundColor: 'rgba(255,255,255,0.04)',
    paddingHorizontal: 14, paddingVertical: 8,
    borderRadius: 8, borderWidth: 1, borderColor: 'rgba(255,255,255,0.08)',
  },
  demoBtnText: { color: 'rgba(255,255,255,0.7)', fontSize: 12, fontWeight: '500' },
});
