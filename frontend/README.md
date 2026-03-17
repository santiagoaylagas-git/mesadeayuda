# Frontend — SOJUS HelpDesk Judicial (React Native)

## Stack

- **Framework:** React Native (Expo ~51)
- **Navegación:** React Navigation 6 (Native Stack + Bottom Tabs)
- **HTTP Client:** Axios con interceptores JWT
- **Estado:** Context API + useReducer (stores modulares)
- **Almacenamiento:** expo-secure-store (JWT seguro)
- **Íconos:** @expo/vector-icons (Ionicons)
- **Testing:** Jest + React Native Testing Library

## Estructura

```
frontend/
├── App.jsx                              # Entry point con providers
├── package.json
├── app.json                             # Expo config
├── babel.config.js
├── jest.config.js                       # Configuración de tests
└── src/
    ├── api/                             # Capa de servicios HTTP
    │   ├── client.js                    # Axios instance + interceptores JWT
    │   ├── authService.js               # Endpoints de autenticación
    │   ├── ticketService.js             # CRUD de tickets
    │   ├── userService.js               # Gestión de usuarios
    │   ├── inventoryService.js          # Hardware/Software
    │   ├── catalogService.js            # Catálogo de servicios
    │   └── index.js                     # Re-exportaciones
    ├── components/
    │   ├── common/                      # Componentes reutilizables
    │   │   ├── Badge.jsx
    │   │   ├── Button.jsx
    │   │   ├── Card.jsx
    │   │   ├── EmptyState.jsx
    │   │   ├── ErrorBoundary.jsx
    │   │   ├── Input.jsx
    │   │   ├── LoadingSpinner.jsx
    │   │   ├── Modal.jsx
    │   │   ├── SearchBar.jsx
    │   │   └── Toast.jsx
    │   ├── layout/
    │   │   ├── Header.jsx
    │   │   └── SafeWrapper.jsx
    │   ├── tickets/
    │   │   ├── TicketCard.jsx
    │   │   ├── TicketForm.jsx
    │   │   ├── TicketPriorityBadge.jsx
    │   │   ├── TicketStatusBadge.jsx
    │   │   └── TicketTimeline.jsx
    │   └── index.js                     # Re-exportaciones
    ├── hooks/
    │   ├── useAuth.js                   # Acceso al AuthContext
    │   ├── useForm.js                   # Validación de formularios
    │   ├── usePagination.js             # Paginación infinita
    │   ├── usePermissions.js            # RBAC por rol
    │   └── useTickets.js                # Lógica de tickets
    ├── navigation/
    │   ├── AppNavigator.jsx             # Root: Auth vs Main
    │   ├── AuthNavigator.jsx            # Login + ChangePassword
    │   ├── MainNavigator.jsx            # Bottom Tabs principal
    │   ├── AdminNavigator.jsx           # Stack de administración
    │   └── linking.js                   # Deep linking config
    ├── screens/
    │   ├── auth/
    │   │   ├── LoginScreen.jsx
    │   │   └── ChangePasswordScreen.jsx
    │   ├── dashboard/
    │   │   └── DashboardScreen.jsx
    │   ├── tickets/
    │   │   ├── TicketListScreen.jsx
    │   │   ├── TicketDetailScreen.jsx
    │   │   ├── CreateTicketScreen.jsx
    │   │   ├── EditTicketScreen.jsx
    │   │   ├── MyTicketsScreen.jsx
    │   │   ├── AssignTechnicianScreen.jsx
    │   │   └── TicketHistoryScreen.jsx
    │   ├── catalog/
    │   │   ├── ServiceCatalogScreen.jsx
    │   │   ├── FAQScreen.jsx
    │   │   └── ServiceRequestScreen.jsx
    │   ├── inventory/
    │   │   ├── AssetListScreen.jsx
    │   │   └── AssetDetailScreen.jsx
    │   └── admin/
    │       ├── UserManagementScreen.jsx
    │       ├── RoleManagementScreen.jsx
    │       ├── TerritorialStructureScreen.jsx
    │       └── ReportsScreen.jsx
    ├── store/
    │   ├── authStore.js                 # Estado de autenticación
    │   ├── ticketStore.js               # Estado de tickets
    │   ├── uiStore.js                   # Loading, toasts, modales
    │   └── index.js
    ├── theme/
    │   ├── colors.js                    # Paleta de colores
    │   ├── spacing.js                   # Espaciado
    │   ├── typography.js                # Tipografía
    │   └── index.js
    └── utils/
        ├── constants.js                 # Roles, permisos, estados
        ├── errorHandler.js              # Manejo de errores
        ├── formatters.js                # Formateo de datos
        ├── storage.js                   # SecureStore helpers
        └── validators.js                # Validaciones de formularios
```

## Setup

```bash
# Requisitos: Node.js 18+, npm, Expo CLI
cd frontend
npm install
npx expo start

# Escanear QR con Expo Go (Android/iOS)
# O presionar 'a' para Android emulator, 'i' para iOS simulator
```

## Configuración API

Crear archivo `.env` en la raíz del proyecto:
```env
EXPO_PUBLIC_API_URL=http://10.0.2.2:8080
```

Valores según entorno:
| Entorno | URL |
|---------|-----|
| Android emulator | `http://10.0.2.2:8080` |
| iOS simulator | `http://localhost:8080` |
| Dispositivo físico | `http://192.168.x.x:8080` |

## Testing

```bash
# Ejecutar tests
npm test

# Ejecutar con cobertura
npm run test:coverage
```

## Arquitectura

- **Navegación:** Bottom Tabs (Dashboard, Tickets, Servicios, Inventario, Admin) con Stack navigators anidados
- **Estado:** 3 stores (Auth, Tickets, UI) con Context API + useReducer
- **Seguridad:** JWT via SecureStore + interceptores Axios + auto-logout en 401
- **Permisos:** Sistema RBAC con 5 roles (ADMINISTRADOR, OPERADOR, TECNICO, GESTOR, AUDITOR)
- **Deep Linking:** Configurado para `sojus://` y `https://sojus.poderjudicial.gob.ar`
