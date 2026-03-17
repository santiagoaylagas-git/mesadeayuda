/**
 * AdminNavigator — Stack de administración SOJUS (solo Admin)
 */
import React from 'react';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { colors } from '../theme/colors';
import UserManagementScreen from '../screens/admin/UserManagementScreen';
import RoleManagementScreen from '../screens/admin/RoleManagementScreen';
import TerritorialStructureScreen from '../screens/admin/TerritorialStructureScreen';
import ReportsScreen from '../screens/admin/ReportsScreen';

const Stack = createNativeStackNavigator();

export default function AdminNavigator() {
  return (
    <Stack.Navigator
      screenOptions={{
        headerStyle: { backgroundColor: colors.primary[900] },
        headerTintColor: colors.white,
        headerTitleStyle: { fontWeight: '600', fontSize: 16 },
      }}
    >
      <Stack.Screen
        name="UserManagement"
        component={UserManagementScreen}
        options={{ title: 'Gestión de Usuarios' }}
      />
      <Stack.Screen
        name="RoleManagement"
        component={RoleManagementScreen}
        options={{ title: 'Gestión de Roles' }}
      />
      <Stack.Screen
        name="TerritorialStructure"
        component={TerritorialStructureScreen}
        options={{ title: 'Estructura Territorial' }}
      />
      <Stack.Screen
        name="Reports"
        component={ReportsScreen}
        options={{ title: 'Reportes' }}
      />
    </Stack.Navigator>
  );
}
