# Plan de Pruebas UI — SOJUS HelpDesk Judicial (Fase 2)

**Proyecto:** Sistema de Gestión de Mesa de Ayuda - Poder Judicial  
**Versión:** 2.0  
**Fecha:** 2026-03-17  
**Responsable:** Santiago Aylagas  
**Plataforma:** React Native (Expo ~51)

---

## 1. Objetivo

Verificar el correcto funcionamiento de la interfaz de usuario de la aplicación móvil SOJUS, cubriendo:

- **Renderizado** de todas las pantallas sin errores
- **Navegación** entre pantallas y tabs correctamente
- **Formularios**: validaciones, envío, feedback visual
- **Control de acceso RBAC**: visibilidad por rol (ADMINISTRADOR, OPERADOR, TECNICO)
- **Interacciones**: filtros, búsqueda, paginación, pull-to-refresh
- **Feedback visual**: loading spinners, toasts, modales, empty states
- **Responsividad**: adaptación a diferentes tamaños de pantalla

---

## 2. Entorno de Pruebas

| Variable | Valor |
|----------|-------|
| Framework | React Native (Expo ~51) |
| Backend | `http://localhost:8080` (con datos de `DataInitializer`) |
| Herramienta de test | Manual + Jest + React Native Testing Library |
| Dispositivos | Android emulator (API 33+), iOS simulator (15+) |

### Credenciales de Prueba

| Usuario | Password | Rol | Tabs Visibles |
|---------|----------|-----|---------------|
| `admin` | `admin123` | ADMINISTRADOR | Dashboard, Tickets, Servicios, Inventario, Admin |
| `operador` | `oper123` | OPERADOR | Dashboard, Tickets, Servicios |
| `tecnico` | `tec123` | TECNICO | Dashboard, Tickets, Servicios, Inventario |

---

## 3. Matriz de Casos de Prueba (68 tests)

### 3.1 Autenticación (10 tests)

| ID | Pantalla | Caso de Prueba | Tipo | Resultado Esperado |
|----|----------|---------------|------|-------------------|
| UI-01 | LoginScreen | Renderizado inicial | Renderizado | Se muestra logo SOJUS, campos usuario/contraseña, botón "Iniciar Sesión" y botones de acceso rápido |
| UI-02 | LoginScreen | Login exitoso con credenciales válidas | Happy Path | Navega al Dashboard. Se muestra nombre del usuario |
| UI-03 | LoginScreen | Login con campos vacíos | Validación | Muestra mensaje de error "Ingresá usuario y contraseña" en recuadro rojo |
| UI-04 | LoginScreen | Login con credenciales inválidas | Error | Muestra mensaje de error "Credenciales inválidas" sin navegar |
| UI-05 | LoginScreen | Botón acceso rápido "Admin" | Happy Path | Autocompleta campos y realiza login como ADMINISTRADOR |
| UI-06 | LoginScreen | Botón acceso rápido "Operador" | Happy Path | Login como OPERADOR — no ve tab Inventario ni Admin |
| UI-07 | LoginScreen | Botón acceso rápido "Técnico" | Happy Path | Login como TECNICO — ve Inventario pero no Admin |
| UI-08 | LoginScreen | Toggle visibilidad de contraseña | Interacción | El campo alterna entre texto oculto y visible |
| UI-09 | LoginScreen | Estado loading durante login | Feedback | Botón muestra spinner y se deshabilita durante la espera |
| UI-10 | ChangePassword | Cambio de contraseña exitoso | Happy Path | Muestra toast de éxito y navega atrás |

### 3.2 Dashboard (8 tests)

| ID | Pantalla | Caso de Prueba | Tipo | Resultado Esperado |
|----|----------|---------------|------|-------------------|
| UI-11 | DashboardScreen | Renderizado con métricas | Renderizado | Se muestran 6 tarjetas de estadísticas con valores numéricos del backend |
| UI-12 | DashboardScreen | Nombre de usuario en header | Renderizado | Muestra "Bienvenido," + nombre + rol del usuario logueado |
| UI-13 | DashboardScreen | Pull-to-refresh | Interacción | Al arrastrar hacia abajo, las métricas se recargan del backend |
| UI-14 | DashboardScreen | Loading spinner inicial | Feedback | Se muestra spinner "Cargando dashboard..." mientras carga |
| UI-15 | DashboardScreen | Acciones rápidas — "Nuevo Ticket" | Navegación | Navega a la pantalla de creación de ticket |
| UI-16 | DashboardScreen | Acciones rápidas — "Ver Tickets" | Navegación | Navega al listado de tickets |
| UI-17 | DashboardScreen | Alerta contratos por vencer | Renderizado | Si hay contratos próximos a vencer, muestra alerta con cantidad |
| UI-18 | DashboardScreen | Botón logout | Interacción | Cierra sesión y navega al LoginScreen |

### 3.3 Tickets — Lista (10 tests)

| ID | Pantalla | Caso de Prueba | Tipo | Resultado Esperado |
|----|----------|---------------|------|-------------------|
| UI-19 | TicketListScreen | Renderizado de lista | Renderizado | Se muestra lista de tickets con ID, asunto, estado, prioridad, fecha |
| UI-20 | TicketListScreen | Barra de búsqueda | Interacción | Al escribir, filtra tickets por asunto/descripción |
| UI-21 | TicketListScreen | Filtro por estado — "Solicitado" | Interacción | Chip se activa (fondo azul) y lista solo muestra tickets en estado SOLICITADO |
| UI-22 | TicketListScreen | Filtro por estado — "Todos" | Interacción | Chip "Todos" se activa y muestra todos los tickets sin filtrar |
| UI-23 | TicketListScreen | Botón "+" crear ticket (ADMIN) | Acceso | Se muestra y navega a CreateTicket |
| UI-24 | TicketListScreen | Botón "+" crear ticket (TECNICO) | Acceso | NO se muestra (TECNICO no tiene permiso `tickets.create`) |
| UI-25 | TicketListScreen | Pull-to-refresh | Interacción | Recarga la lista de tickets del backend |
| UI-26 | TicketListScreen | Empty state sin tickets | Feedback | Muestra ícono, mensaje "Sin tickets" y botón "Crear Ticket" si tiene permiso |
| UI-27 | TicketListScreen | Tap en ticket | Navegación | Navega a TicketDetailScreen con datos del ticket seleccionado |
| UI-28 | TicketListScreen | Contador de tickets | Renderizado | Muestra "N ticket(s)" con la cantidad correcta |

### 3.4 Tickets — Detalle y Acciones (10 tests)

| ID | Pantalla | Caso de Prueba | Tipo | Resultado Esperado |
|----|----------|---------------|------|-------------------|
| UI-29 | TicketDetailScreen | Renderizado de datos completos | Renderizado | Muestra asunto, descripción, estado, prioridad, técnico asignado, fechas |
| UI-30 | TicketDetailScreen | Badge de estado con color correcto | Renderizado | SOLICITADO=azul, ASIGNADO=naranja, EN_CURSO=azul, CERRADO=verde |
| UI-31 | TicketDetailScreen | Badge de prioridad con color correcto | Renderizado | ALTA=rojo, MEDIA=naranja, BAJA=verde, CRITICA=rojo oscuro |
| UI-32 | TicketDetailScreen | Cambio de estado válido | Happy Path | Transición SOLICITADO→ASIGNADO se realiza y se actualiza la UI |
| UI-33 | TicketDetailScreen | Cambio de estado inválido | Error | Transición no permitida muestra toast de error |
| UI-34 | TicketDetailScreen | Timeline de estados | Renderizado | Muestra historial de cambios de estado en orden cronológico |
| UI-35 | CreateTicketScreen | Renderizado del formulario | Renderizado | Se muestra formulario con campos: asunto, descripción, prioridad, juzgado |
| UI-36 | CreateTicketScreen | Envío exitoso | Happy Path | Crea ticket, muestra toast de éxito, navega a la lista |
| UI-37 | CreateTicketScreen | Validación de campos obligatorios | Validación | Muestra errores en campos vacíos al intentar enviar |
| UI-38 | EditTicketScreen | Pre-carga datos del ticket | Renderizado | Formulario se carga con los datos actuales del ticket |

### 3.5 Tickets — Asignación y Mis Tickets (5 tests)

| ID | Pantalla | Caso de Prueba | Tipo | Resultado Esperado |
|----|----------|---------------|------|-------------------|
| UI-39 | AssignTechnicianScreen | Lista de técnicos disponibles | Renderizado | Muestra lista de usuarios con rol TECNICO |
| UI-40 | AssignTechnicianScreen | Asignación exitosa | Happy Path | Asigna técnico, muestra toast y navega al detalle |
| UI-41 | MyTicketsScreen | Tickets del técnico logueado | Renderizado | Solo muestra tickets asignados al técnico actual |
| UI-42 | TicketHistoryScreen | Historial de auditoría | Renderizado | Muestra cambios realizados al ticket con fecha/usuario/acción |
| UI-43 | MyTicketsScreen | Empty state sin tickets asignados | Feedback | Muestra mensaje "No tienes tickets asignados" |

### 3.6 Catálogo de Servicios (5 tests)

| ID | Pantalla | Caso de Prueba | Tipo | Resultado Esperado |
|----|----------|---------------|------|-------------------|
| UI-44 | ServiceCatalogScreen | Renderizado del catálogo | Renderizado | Se muestra lista de servicios disponibles |
| UI-45 | ServiceCatalogScreen | Tap en servicio | Navegación | Navega a ServiceRequestScreen con datos del servicio |
| UI-46 | FAQScreen | Renderizado de preguntas frecuentes | Renderizado | FAQ se muestra con formato de acordeón expandible |
| UI-47 | ServiceRequestScreen | Envío de solicitud exitoso | Happy Path | Crea solicitud, muestra confirmación |
| UI-48 | ServiceRequestScreen | Validación de formulario | Validación | Campos obligatorios muestran error si están vacíos |

### 3.7 Inventario (5 tests)

| ID | Pantalla | Caso de Prueba | Tipo | Resultado Esperado |
|----|----------|---------------|------|-------------------|
| UI-49 | AssetListScreen | Renderizado de lista de activos | Renderizado | Muestra lista de hardware/software con tipo, nombre, estado |
| UI-50 | AssetListScreen | Filtro por tipo (Hardware/Software) | Interacción | Tabs o chips permiten filtrar entre hardware y software |
| UI-51 | AssetListScreen | Tap en activo | Navegación | Navega a AssetDetailScreen con datos del activo |
| UI-52 | AssetDetailScreen | Datos completos del activo | Renderizado | Muestra inventario patrimonial, marca, modelo, ubicación |
| UI-53 | AssetListScreen | Visibilidad (OPERADOR) | Acceso | Tab Inventario NO está visible para OPERADOR |

### 3.8 Administración (7 tests)

| ID | Pantalla | Caso de Prueba | Tipo | Resultado Esperado |
|----|----------|---------------|------|-------------------|
| UI-54 | AdminTab | Visibilidad solo para ADMIN | Acceso | Tab "Admin" solo aparece para usuarios ADMINISTRADOR |
| UI-55 | UserManagementScreen | Lista de usuarios | Renderizado | Muestra tabla/lista de usuarios con nombre, rol, estado |
| UI-56 | UserManagementScreen | Crear nuevo usuario | Happy Path | Formulario crea usuario y muestra confirmación |
| UI-57 | UserManagementScreen | Validación de usuario duplicado | Error | Muestra error si el username ya existe |
| UI-58 | RoleManagementScreen | Lista de roles y permisos | Renderizado | Muestra roles (ADMIN, OPERADOR, TECNICO, GESTOR, AUDITOR) |
| UI-59 | TerritorialStructureScreen | Estructura de circunscripciones | Renderizado | Muestra circunscripciones → edificios → juzgados |
| UI-60 | ReportsScreen | Renderizado de reportes | Renderizado | Muestra métricas y gráficos de gestión |

### 3.9 Navegación Global (8 tests)

| ID | Caso de Prueba | Tipo | Resultado Esperado |
|----|---------------|------|-------------------|
| UI-61 | Bottom Tabs visibles post-login | Navegación | Se muestran los tabs correspondientes al rol del usuario |
| UI-62 | Navegación entre tabs | Navegación | Al tocar cada tab, se muestra la pantalla correspondiente |
| UI-63 | Tab activo con color diferenciado | Renderizado | Tab activo tiene ícono y texto en color `accent`; inactivos en gris |
| UI-64 | Deep link `sojus://tickets/1` | Navegación | Abre directamente el detalle del ticket con id=1 |
| UI-65 | Sesión persistente tras cerrar app | Sesión | Al reabrir la app, el usuario sigue logueado sin pedir credenciales |
| UI-66 | Auto-logout en token expirado | Sesión | Al recibir 401 del backend, navega automáticamente al Login |
| UI-67 | ErrorBoundary en crash de componente | Error | Muestra pantalla de error amigable en vez de crash |
| UI-68 | Toast de éxito/error | Feedback | Toasts aparecen en la parte inferior con colores apropiados (verde/rojo/azul) |

---

## 4. Distribución por Categoría

| Categoría | Cantidad | Descripción |
|-----------|----------|-------------|
| **Happy Path** | 20 | Flujos exitosos completos |
| **Renderizado** | 22 | Verificación visual de datos y componentes |
| **Interacción** | 7 | Filtros, búsqueda, pull-to-refresh |
| **Navegación** | 7 | Transiciones entre pantallas y tabs |
| **Validación** | 3 | Formularios con campos requeridos |
| **Control de Acceso** | 4 | Visibilidad de elementos según rol RBAC |
| **Feedback** | 3 | Loading, empty states, toasts |
| **Error** | 2 | Manejo de errores y estados inválidos |
| **Total** | **68** | |

---

## 5. Distribución por Módulo

| Módulo | Pantallas | Tests | Cobertura |
|--------|-----------|-------|-----------|
| Autenticación | LoginScreen, ChangePassword | 10 | Login, validación, acceso rápido, toggle password |
| Dashboard | DashboardScreen | 8 | Métricas, acciones rápidas, alertas, refresh |
| Tickets — Lista | TicketListScreen | 10 | Búsqueda, filtros, RBAC, empty state |
| Tickets — Detalle | TicketDetail, Create, Edit | 10 | CRUD, validaciones, estados, timeline |
| Tickets — Asignación | Assign, MyTickets, History | 5 | Asignación, historial, filtrado por técnico |
| Catálogo | ServiceCatalog, FAQ, Request | 5 | Listado, solicitud, FAQ |
| Inventario | AssetList, AssetDetail | 5 | Hardware/Software, detalle, RBAC |
| Administración | Users, Roles, Territorial, Reports | 7 | CRUD usuarios, roles, estructura, reportes |
| Navegación Global | - | 8 | Tabs, deep links, sesión, error boundary |
| **Total** | **26 pantallas** | **68** | |

---

## 6. Criterios de Aceptación

| Criterio | Umbral |
|----------|--------|
| Tests pasados | **100%** (68/68) |
| Cobertura de pantallas | 26/26 pantallas cubiertas |
| Cobertura de roles | 3 roles principales probados (ADMIN, OPERADOR, TECNICO) |
| Tiempo de carga | Dashboard < 2s, listas < 3s |
| Tipos de prueba | Renderizado + Interacción + Navegación + RBAC + Validación + Error |
| Dispositivos | Android emulator + iOS simulator |

---

## 7. Instrucciones de Ejecución

### Pruebas Manuales

#### Prerrequisitos
1. Backend corriendo en `http://localhost:8080` con datos de prueba
2. Frontend ejecutándose via `npx expo start`
3. Emulador Android o simulador iOS configurado

#### Pasos de Ejecución
1. **Iniciar backend**: `cd backend && mvn spring-boot:run`
2. **Iniciar frontend**: `cd frontend && npx expo start`
3. **Abrir app** en emulador/simulador
4. **Ejecutar tests** en orden:
   - **Sección 3.1**: Tests de autenticación (UI-01 a UI-10)
   - **Sección 3.2**: Tests de dashboard (UI-11 a UI-18)
   - **Sección 3.3-3.5**: Tests de tickets (UI-19 a UI-43)
   - **Sección 3.6**: Tests de catálogo (UI-44 a UI-48)
   - **Sección 3.7**: Tests de inventario (UI-49 a UI-53)
   - **Sección 3.8**: Tests de administración (UI-54 a UI-60)
   - **Sección 3.9**: Tests de navegación global (UI-61 a UI-68)
5. **Probar con 3 roles**: Repetir flujos clave con admin, operador y técnico
6. **Registrar resultados**: ✅ pasado / ❌ fallido con evidencia (screenshot)

### Ejecución Secuencial Obligatoria
> **IMPORTANTE**: Los tests de autenticación (Sección 3.1) deben ejecutarse primero, ya que el resto de las secciones requieren un usuario logueado.

### Pruebas Automatizadas (Jest + Testing Library)

```bash
cd frontend
npm test              # Ejecutar todos los tests
npm run test:coverage # Ejecutar con cobertura
```

---

## 8. Artefactos

| Archivo | Descripción |
|---------|-------------|
| `docs/api/plan_de_pruebas_ui.md` | Este documento |
| `docs/api/plan_de_pruebas_backend.md` | Plan de pruebas del backend (57 endpoints) |
| `frontend/jest.config.js` | Configuración de Jest para tests automatizados |
