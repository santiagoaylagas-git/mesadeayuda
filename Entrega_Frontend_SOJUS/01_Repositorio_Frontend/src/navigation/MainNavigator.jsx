/**
 * MainNavigator — Navegación principal con Bottom Tabs SOJUS
 * Dashboard, Tickets, Catálogo, Admin (condicional), Perfil
 */
import React from 'react';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { Ionicons } from '@expo/vector-icons';
import { colors } from '../theme/colors';
import usePermissions from '../hooks/usePermissions';

// Screens — Dashboard
import DashboardScreen from '../screens/dashboard/DashboardScreen';

// Screens — Tickets
import TicketListScreen from '../screens/tickets/TicketListScreen';
import TicketDetailScreen from '../screens/tickets/TicketDetailScreen';
import CreateTicketScreen from '../screens/tickets/CreateTicketScreen';
import EditTicketScreen from '../screens/tickets/EditTicketScreen';
import MyTicketsScreen from '../screens/tickets/MyTicketsScreen';
import AssignTechnicianScreen from '../screens/tickets/AssignTechnicianScreen';
import TicketHistoryScreen from '../screens/tickets/TicketHistoryScreen';

// Screens — Catalog
import ServiceCatalogScreen from '../screens/catalog/ServiceCatalogScreen';
import FAQScreen from '../screens/catalog/FAQScreen';
import ServiceRequestScreen from '../screens/catalog/ServiceRequestScreen';

// Screens — Inventory
import AssetListScreen from '../screens/inventory/AssetListScreen';
import AssetDetailScreen from '../screens/inventory/AssetDetailScreen';

// Screens — Admin
import AdminNavigator from './AdminNavigator';

const Tab = createBottomTabNavigator();
const TicketStack = createNativeStackNavigator();
const DashboardStack = createNativeStackNavigator();
const CatalogStack = createNativeStackNavigator();
const InventoryStack = createNativeStackNavigator();

const screenOptions = {
  headerStyle: { backgroundColor: colors.primary[900] },
  headerTintColor: colors.white,
  headerTitleStyle: { fontWeight: '600', fontSize: 16 },
};

/** Stack de Dashboard */
function DashboardStackScreen() {
  return (
    <DashboardStack.Navigator screenOptions={screenOptions}>
      <DashboardStack.Screen
        name="Dashboard"
        component={DashboardScreen}
        options={{ title: 'Panel Principal' }}
      />
    </DashboardStack.Navigator>
  );
}

/** Stack de Tickets */
function TicketStackScreen() {
  return (
    <TicketStack.Navigator screenOptions={screenOptions}>
      <TicketStack.Screen
        name="TicketList"
        component={TicketListScreen}
        options={{ title: 'Mesa de Ayuda' }}
      />
      <TicketStack.Screen
        name="TicketDetail"
        component={TicketDetailScreen}
        options={{ title: 'Detalle del Ticket' }}
      />
      <TicketStack.Screen
        name="CreateTicket"
        component={CreateTicketScreen}
        options={{ title: 'Nuevo Ticket' }}
      />
      <TicketStack.Screen
        name="EditTicket"
        component={EditTicketScreen}
        options={{ title: 'Editar Ticket' }}
      />
      <TicketStack.Screen
        name="MyTickets"
        component={MyTicketsScreen}
        options={{ title: 'Mis Tickets' }}
      />
      <TicketStack.Screen
        name="AssignTechnician"
        component={AssignTechnicianScreen}
        options={{ title: 'Asignar Técnico' }}
      />
      <TicketStack.Screen
        name="TicketHistory"
        component={TicketHistoryScreen}
        options={{ title: 'Historial del Ticket' }}
      />
    </TicketStack.Navigator>
  );
}

/** Stack de Catálogo */
function CatalogStackScreen() {
  return (
    <CatalogStack.Navigator screenOptions={screenOptions}>
      <CatalogStack.Screen
        name="ServiceCatalog"
        component={ServiceCatalogScreen}
        options={{ title: 'Catálogo de Servicios' }}
      />
      <CatalogStack.Screen
        name="FAQ"
        component={FAQScreen}
        options={{ title: 'Preguntas Frecuentes' }}
      />
      <CatalogStack.Screen
        name="ServiceRequest"
        component={ServiceRequestScreen}
        options={{ title: 'Solicitar Servicio' }}
      />
    </CatalogStack.Navigator>
  );
}

/** Stack de Inventario */
function InventoryStackScreen() {
  return (
    <InventoryStack.Navigator screenOptions={screenOptions}>
      <InventoryStack.Screen
        name="AssetList"
        component={AssetListScreen}
        options={{ title: 'Inventario' }}
      />
      <InventoryStack.Screen
        name="AssetDetail"
        component={AssetDetailScreen}
        options={{ title: 'Detalle del Activo' }}
      />
    </InventoryStack.Navigator>
  );
}

/** Tab Navigator principal */
export default function MainNavigator() {
  const { isAdmin, hasPermission } = usePermissions();

  return (
    <Tab.Navigator
      screenOptions={({ route }) => ({
        headerShown: false,
        tabBarIcon: ({ focused, color, size }) => {
          const icons = {
            DashboardTab: focused ? 'grid' : 'grid-outline',
            TicketsTab: focused ? 'ticket' : 'ticket-outline',
            CatalogTab: focused ? 'book' : 'book-outline',
            InventoryTab: focused ? 'hardware-chip' : 'hardware-chip-outline',
            AdminTab: focused ? 'settings' : 'settings-outline',
          };
          return <Ionicons name={icons[route.name]} size={size} color={color} />;
        },
        tabBarActiveTintColor: colors.accent,
        tabBarInactiveTintColor: colors.textSecondary,
        tabBarStyle: {
          backgroundColor: colors.surface,
          borderTopColor: colors.border,
          paddingTop: 4,
          height: 60,
        },
        tabBarLabelStyle: {
          fontSize: 11,
          fontWeight: '500',
          paddingBottom: 4,
        },
      })}
    >
      <Tab.Screen
        name="DashboardTab"
        component={DashboardStackScreen}
        options={{ title: 'Inicio' }}
      />
      <Tab.Screen
        name="TicketsTab"
        component={TicketStackScreen}
        options={{ title: 'Tickets' }}
      />
      <Tab.Screen
        name="CatalogTab"
        component={CatalogStackScreen}
        options={{ title: 'Servicios' }}
      />
      {hasPermission('inventory.read') && (
        <Tab.Screen
          name="InventoryTab"
          component={InventoryStackScreen}
          options={{ title: 'Inventario' }}
        />
      )}
      {isAdmin && (
        <Tab.Screen
          name="AdminTab"
          component={AdminNavigator}
          options={{ title: 'Admin', headerShown: false }}
        />
      )}
    </Tab.Navigator>
  );
}
