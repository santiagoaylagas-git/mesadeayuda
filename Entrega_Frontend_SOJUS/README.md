# 📦 Entrega — SOJUS HelpDesk Judicial · Frontend

**Proyecto:** Sistema de Gestión de Mesa de Ayuda — Poder Judicial  
**Autor:** Santiago Aylagas  
**Fecha de entrega:** 17 de Marzo de 2026  
**Versión:** 2.0.0

---

## 📁 Contenido del Paquete

| Carpeta | Descripción |
|---------|-------------|
| `01_Repositorio_Frontend/` | Código fuente completo de la aplicación móvil (React Native / Expo ~51) |
| `02_Plan_de_Pruebas_UI/` | Documento del plan de pruebas de interfaz de usuario (68 casos de prueba) |
| `03_Docker/` | Archivo Dockerfile para contenerización del frontend |

---

## 🛠️ Stack Tecnológico

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| React Native | 0.74.5 | Framework multiplataforma |
| Expo | ~51.0.0 | Toolchain de desarrollo |
| React Navigation | 6.x | Navegación (Stack + Bottom Tabs) |
| Axios | 1.7.2 | Cliente HTTP con interceptores JWT |
| Jest | 29.x | Testing unitario |
| React Native Testing Library | 12.x | Testing de componentes |

---

## 🏗️ Arquitectura del Proyecto

```
frontend/
├── App.jsx                    # Punto de entrada
├── src/
│   ├── api/                   # Capa de servicios HTTP (Axios + JWT)
│   ├── components/            # Componentes reutilizables (Badge, Card, Button, etc.)
│   ├── hooks/                 # Custom hooks (auth, permisos RBAC, paginación)
│   ├── navigation/            # Navegadores (Auth, Main, Admin, Deep Linking)
│   ├── screens/               # 26 pantallas organizadas por módulo
│   ├── store/                 # Estado global (Context API + useReducer)
│   ├── theme/                 # Sistema de diseño (colores, tipografía, espaciado)
│   └── utils/                 # Utilidades (validadores, formatters, constantes)
├── package.json
├── jest.config.js
└── Dockerfile
```

---

## 🚀 Instrucciones de Ejecución

### Opción 1: Ejecución Local

```bash
# Prerrequisitos: Node.js 18+, npm, Expo CLI
cd 01_Repositorio_Frontend
npm install
npx expo start

# Escanear QR con Expo Go o presionar 'a' (Android) / 'i' (iOS)
```

### Opción 2: Docker

```bash
cd 03_Docker
docker build -t sojus-frontend .
docker run -p 19006:19006 sojus-frontend
```

---

## 🧪 Plan de Pruebas UI

El plan de pruebas está ubicado en `02_Plan_de_Pruebas_UI/plan_de_pruebas_ui.md` y cubre:

| Métrica | Valor |
|---------|-------|
| Total de casos de prueba | **68** |
| Pantallas cubiertas | **26** |
| Roles probados | 3 (ADMINISTRADOR, OPERADOR, TÉCNICO) |
| Categorías de prueba | Renderizado, Happy Path, Interacción, Navegación, Validación, RBAC, Feedback, Error |

### Ejecución de Tests Automatizados

```bash
cd 01_Repositorio_Frontend
npm install
npm test                # Ejecutar tests
npm run test:coverage   # Con reporte de cobertura
```

---

## 📋 Resumen de Módulos

| Módulo | Pantallas | Tests |
|--------|-----------|-------|
| Autenticación | Login, Cambio Contraseña | 10 |
| Dashboard | Panel Principal | 8 |
| Tickets — Lista | Listado, Búsqueda, Filtros | 10 |
| Tickets — Detalle | CRUD, Estados, Timeline | 10 |
| Tickets — Asignación | Asignación, Mis Tickets, Historial | 5 |
| Catálogo de Servicios | Catálogo, FAQ, Solicitudes | 5 |
| Inventario | Activos HW/SW, Detalle | 5 |
| Administración | Usuarios, Roles, Estructura, Reportes | 7 |
| Navegación Global | Tabs, Deep Links, Sesión | 8 |

---

> **Nota:** El backend debe estar corriendo en `http://localhost:8080` para que la aplicación funcione correctamente. Consultar la documentación del backend para instrucciones de configuración.
