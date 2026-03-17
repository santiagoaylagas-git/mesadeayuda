/**
 * App.jsx — Entry point de la aplicación SOJUS
 * Wraps con todos los providers y configura navegación
 */
import React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { SafeAreaProvider } from 'react-native-safe-area-context';
import { StatusBar } from 'expo-status-bar';
import Toast from 'react-native-toast-message';

import { AuthProvider } from './src/store/authStore';
import { TicketProvider } from './src/store/ticketStore';
import { UIProvider } from './src/store/uiStore';
import { ErrorBoundary } from './src/components';
import { toastConfig } from './src/components/common/Toast';
import AppNavigator from './src/navigation/AppNavigator';
import linking from './src/navigation/linking';

export default function App() {
  return (
    <SafeAreaProvider>
      <AuthProvider>
        <TicketProvider>
          <UIProvider>
            <ErrorBoundary>
              <NavigationContainer linking={linking}>
                <StatusBar style="light" />
                <AppNavigator />
              </NavigationContainer>
            </ErrorBoundary>
          </UIProvider>
        </TicketProvider>
      </AuthProvider>
      <Toast config={toastConfig} position="bottom" bottomOffset={80} />
    </SafeAreaProvider>
  );
}
