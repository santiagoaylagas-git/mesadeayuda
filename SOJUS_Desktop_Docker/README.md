# 📦 SOJUS HelpDesk Judicial — Entrega Frontend + Docker

**Proyecto:** Sistema de Gestión de Mesa de Ayuda — Poder Judicial  
**Autor:** Santiago Aylagas  
**Fecha:** 17 de Marzo de 2026  
**Versión:** 2.0.0

---

## 📁 Contenido

| Carpeta / Archivo | Descripción |
|-------------------|-------------|
| `01_Repositorio_Frontend/` | Código fuente completo (React Native / Expo ~51) — 26 pantallas, 10 módulos |
| `02_Plan_de_Pruebas_UI/` | Plan de pruebas de interfaz de usuario — **68 casos de prueba** |
| `Dockerfile` | Imagen Docker multi-stage con health check |
| `docker-compose.yml` | Orquestación con un solo comando |
| `.dockerignore` | Exclusiones del build context |

---

## 🐳 Ejecución con Docker

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

| Usuario | Contraseña | Rol |
|---------|------------|-----|
| `admin` | `admin123` | ADMINISTRADOR |
| `operador` | `oper123` | OPERADOR |
| `tecnico` | `tec123` | TÉCNICO |

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
| Docker | 20+ | Contenerización |

---

## 🏗️ Arquitectura

```
01_Repositorio_Frontend/
├── App.jsx                    # Punto de entrada
├── src/
│   ├── api/                   # Servicios HTTP (Axios + JWT)
│   ├── components/            # Componentes reutilizables
│   ├── hooks/                 # Custom hooks (auth, RBAC, paginación)
│   ├── navigation/            # Navegadores (Auth, Main, Admin)
│   ├── screens/               # 26 pantallas por módulo
│   ├── store/                 # Estado global (Context + useReducer)
│   ├── theme/                 # Sistema de diseño
│   └── utils/                 # Utilidades y constantes
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

### Tests Automatizados

```bash
cd 01_Repositorio_Frontend
npm install
npm test                # Ejecutar tests
npm run test:coverage   # Con reporte de cobertura
```

---

## 📋 Módulos

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
