# Changelog — SOJUS HelpDesk Judicial · Frontend

## [2.0.0] — 2026-03-17

### Funcionalidades
- ✅ 26 pantallas organizadas en 9 módulos funcionales
- ✅ Sistema RBAC con 5 roles y permisos granulares
- ✅ Navegación con Bottom Tabs y Stack Navigators anidados
- ✅ Deep linking (`sojus://tickets/1`, `sojus://inventory/1`)
- ✅ Estado global con Context API + useReducer (3 stores)
- ✅ Autenticación JWT con SecureStore + auto-logout en 401
- ✅ Pull-to-refresh, paginación infinita, búsqueda, filtros
- ✅ ErrorBoundary global y manejo de errores i18n (español)
- ✅ 17 componentes reutilizables con sistema de diseño institucional
- ✅ Docker Compose para despliegue con un solo comando

### Testing
- ✅ Plan de pruebas UI con 68 casos de prueba
- ✅ Cobertura de 26 pantallas en 3 roles (ADMIN, OPERADOR, TÉCNICO)
- ✅ Configuración Jest + React Native Testing Library

### Stack
- React Native 0.74.5 + Expo ~51
- React Navigation 6 (Stack + Bottom Tabs)
- Axios 1.7.2 con interceptores JWT
- Jest 29 + @testing-library/react-native 12
- Node.js 18 + Docker

---

## [1.0.0] — 2026-03-08

### Funcionalidades Iniciales
- MVP con autenticación, dashboard y gestión de tickets
- Backend Spring Boot con REST API
