# 📦 SOJUS HelpDesk Judicial — Entrega Frontend + Docker

**Proyecto:** Sistema de Gestión de Mesa de Ayuda — Poder Judicial  
**Autor:** Santiago Aylagas  
**Fecha:** 17 de Marzo de 2026  
**Versión:** 2.0.0

---

## 📁 Contenido del Paquete

| Carpeta / Archivo | Descripción |
|-------------------|-------------|
| `01_Repositorio_Frontend/` | Código fuente completo (React Native / Expo ~51) — 26 pantallas, 10 módulos |
| `02_Plan_de_Pruebas_UI/` | Plan de pruebas de interfaz de usuario — **68 casos de prueba** |
| `Dockerfile` | Imagen Docker con health check |
| `docker-compose.yml` | Orquestación con un solo comando |
| `CHANGELOG.md` | Historial de versiones del proyecto |
| `.dockerignore` | Exclusiones del build context |

---

## 🐳 Ejecución con Docker

### Requisitos del Sistema

| Requisito | Versión Mínima |
|-----------|---------------|
| Docker Engine | 20.10+ |
| Docker Compose | 2.0+ |
| RAM disponible | 2 GB |
| Disco | 500 MB |

### Opción 1 — Docker Compose (recomendado)

```bash
docker-compose up --build
```

### Opción 2 — Docker Manual

```bash
docker build -t sojus-frontend .
docker run -p 19006:19006 sojus-frontend
```

### Acceder a la Aplicación

Abrir en el navegador: **http://localhost:19006**

### Credenciales de Prueba

| Usuario | Contraseña | Rol | Tabs Visibles |
|---------|------------|-----|---------------|
| `admin` | `admin123` | ADMINISTRADOR | Inicio, Tickets, Servicios, Inventario, Admin |
| `operador` | `oper123` | OPERADOR | Inicio, Tickets, Servicios |
| `tecnico` | `tec123` | TÉCNICO | Inicio, Tickets, Servicios, Inventario |

> ⚠️ **Requisito:** El backend debe estar corriendo en `http://localhost:8080`.

---

## 🛠️ Stack Tecnológico

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| React Native | 0.74.5 | Framework multiplataforma |
| Expo | ~51.0.0 | Toolchain de desarrollo |
| React Navigation | 6.x | Navegación (Stack + Bottom Tabs) |
| Axios | 1.7.2 | Cliente HTTP con interceptores JWT |
| Jest | 29.x | Testing unitario |
| Node.js | 18 LTS | Runtime |
| Docker | 20+ | Contenerización |

---

## 🏗️ Arquitectura del Proyecto

```
01_Repositorio_Frontend/
├── App.jsx                      # Punto de entrada con providers
├── src/
│   ├── api/                     # 6 servicios HTTP (Axios + JWT)
│   │   ├── client.js            # Instancia Axios + interceptores JWT
│   │   ├── authService.js       # Login, logout, change-password
│   │   ├── ticketService.js     # CRUD tickets + estados + asignación
│   │   ├── userService.js       # Gestión de usuarios
│   │   ├── inventoryService.js  # Hardware / Software
│   │   └── catalogService.js    # Catálogo de servicios
│   ├── components/              # 17 componentes reutilizables
│   │   ├── common/              # Badge, Button, Card, EmptyState, ErrorBoundary, Input, LoadingSpinner, Modal, SearchBar, Toast
│   │   ├── layout/              # Header, SafeWrapper
│   │   └── tickets/             # TicketCard, TicketForm, TicketStatusBadge, TicketPriorityBadge, TicketTimeline
│   ├── hooks/                   # 5 custom hooks
│   │   ├── useAuth.js           # Acceso al AuthContext
│   │   ├── useForm.js           # Validación de formularios (touched, errors)
│   │   ├── usePagination.js     # Paginación infinita
│   │   ├── usePermissions.js    # RBAC por rol (5 roles)
│   │   └── useTickets.js        # Lógica de tickets
│   ├── navigation/              # 4 navegadores + deep linking
│   │   ├── AppNavigator.jsx     # Root: Auth vs Main (isAuthenticated)
│   │   ├── AuthNavigator.jsx    # Login + ChangePassword
│   │   ├── MainNavigator.jsx    # Bottom Tabs con RBAC filtering
│   │   ├── AdminNavigator.jsx   # Stack de administración
│   │   └── linking.js           # sojus:// deep linking
│   ├── screens/                 # 26 pantallas en 5 módulos
│   │   ├── auth/                # LoginScreen, ChangePasswordScreen
│   │   ├── dashboard/           # DashboardScreen (métricas reales)
│   │   ├── tickets/             # 7 pantallas (lista, detalle, CRUD, asignación, historial)
│   │   ├── catalog/             # ServiceCatalog, FAQ, ServiceRequest
│   │   ├── inventory/           # AssetList, AssetDetail
│   │   └── admin/               # Users, Roles, Territorial, Reports
│   ├── store/                   # 3 stores (Context + useReducer)
│   │   ├── authStore.js         # Sesión (JWT + user data + auto-restore)
│   │   ├── ticketStore.js       # Tickets (CRUD + paginación + filtros)
│   │   └── uiStore.js           # UI (loading, toasts, modales)
│   ├── theme/                   # Sistema de diseño institucional
│   │   ├── colors.js            # Paleta judicial (primarios, semánticos, prioridades)
│   │   ├── spacing.js           # Espaciado
│   │   └── typography.js        # Tipografía
│   └── utils/                   # Utilidades
│       ├── constants.js         # Roles, permisos RBAC, estados, transiciones
│       ├── errorHandler.js      # Mensajes i18n español (400-503)
│       ├── formatters.js        # Fechas (dayjs), roles, ticket IDs
│       ├── storage.js           # SecureStore (JWT + user data)
│       └── validators.js        # Validación de formularios
├── .env.example                 # Variables de entorno documentadas
├── package.json
└── jest.config.js
```

---

## 🧪 Plan de Pruebas UI — Resumen

Documento completo: `02_Plan_de_Pruebas_UI/plan_de_pruebas_ui.md`

| Métrica | Valor |
|---------|-------|
| Total de casos de prueba | **68** |
| Pantallas cubiertas | **26** |
| Roles probados | 3 (ADMIN, OPERADOR, TÉCNICO) |
| Categorías | Renderizado, Happy Path, Interacción, Navegación, Validación, RBAC, Feedback, Error |

### Distribución por Módulo

| Módulo | Pantallas | Tests |
|--------|-----------|-------|
| Autenticación | Login, Cambio Contraseña | 10 |
| Dashboard | Panel Principal | 8 |
| Tickets — Lista | Listado, Búsqueda, Filtros | 10 |
| Tickets — Detalle | CRUD, Estados, Timeline | 10 |
| Tickets — Asignación | Asignación, Mis Tickets | 5 |
| Catálogo de Servicios | Catálogo, FAQ, Solicitudes | 5 |
| Inventario | Activos HW/SW | 5 |
| Administración | Usuarios, Roles, Estructura | 7 |
| Navegación Global | Tabs, Deep Links, Sesión | 8 |
| **Total** | **26** | **68** |

### Ejecución de Tests

```bash
cd 01_Repositorio_Frontend
npm install
npm test                # Ejecutar tests
npm run test:coverage   # Con reporte de cobertura
```

---

## 🔧 Ejecución Local (sin Docker)

```bash
# Prerrequisitos: Node.js 18+, npm
cd 01_Repositorio_Frontend
cp .env.example .env          # Configurar API URL
npm install
npx expo start

# Escanear QR con Expo Go o presionar:
# 'a' → Android emulator
# 'i' → iOS simulator
# 'w' → Web browser
```

---

## 🛡️ Seguridad

| Feature | Implementación |
|---------|---------------|
| JWT Storage | expo-secure-store (cifrado nativo) |
| Auto-logout | Interceptor 401 en Axios |
| RBAC | 5 roles con permisos granulares |
| Session Restore | Token + user data persistentes |
| Input Validation | Validadores client-side + server-side |

---

## ❓ Troubleshooting

| Problema | Solución |
|----------|----------|
| `Cannot connect to backend` | Verificar que el backend corre en `http://localhost:8080` |
| Docker build falla | Verificar Docker 20+ instalado: `docker --version` |
| Puerto 19006 ocupado | Cambiar puerto en `docker-compose.yml`: `"19007:19006"` |
| Expo no inicia | Eliminar `node_modules` y ejecutar `npm ci --legacy-peer-deps` |

---

> **Nota:** Este paquete contiene el código fuente completo, el plan de pruebas y la configuración Docker. Para el backend, consultar la documentación del repositorio backend.
