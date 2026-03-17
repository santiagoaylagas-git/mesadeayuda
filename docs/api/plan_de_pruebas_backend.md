# Plan de Pruebas Backend — SOJUS HelpDesk Judicial (Fase 2)

**Proyecto:** Sistema de Gestión de Mesa de Ayuda - Poder Judicial  
**Versión:** 2.0  
**Fecha:** 2026-03-06  
**Responsable:** Santiago Aylagas

---

## 1. Objetivo

Verificar el correcto funcionamiento de todos los endpoints REST del backend SOJUS, cubriendo:

- **Flujos positivos** (happy path): CRUD completo de cada entidad
- **Validaciones**: Campos obligatorios, formatos, unicidad
- **Control de acceso**: Permisos por rol (ADMINISTRADOR, OPERADOR, TECNICO)
- **Errores controlados**: Recursos inexistentes, transiciones inválidas, datos duplicados
- **Performance**: Tiempos de respuesta < 2 segundos

---

## 2. Configuración del Entorno

| Variable | Valor |
|----------|-------|
| `base_url` | `http://localhost:8080` |
| Base de datos | H2 en memoria (datos de prueba via `DataInitializer`) |
| Autenticación | JWT Bearer Token |

### Credenciales de Prueba

| Usuario | Password | Rol | Permisos |
|---------|----------|-----|----------|
| `admin` | `admin123` | ADMINISTRADOR | Acceso total |
| `operador` | `oper123` | OPERADOR | Tickets (crear), lectura general |
| `tecnico` | `tec123` | TECNICO | Tickets (cambiar estado), lectura inventario |

---

## 3. Matriz de Casos de Prueba (57 endpoints)

### 3.1 Autenticación (7 tests)

| ID | Endpoint | Método | Categoría | Resultado Esperado |
|----|----------|--------|-----------|-------------------|
| EP-01 | `/api/auth/login` | POST | Happy Path | 200 + token JWT + role=ADMINISTRADOR |
| EP-02 | `/api/auth/login` | POST | Happy Path | 200 + token JWT + role=OPERADOR |
| EP-03 | `/api/auth/login` | POST | Happy Path | 200 + token JWT + role=TECNICO |
| EP-04 | `/api/auth/login` | POST | Error | 401 — Password incorrecta |
| EP-05 | `/api/auth/login` | POST | Error | 401 — Usuario inexistente |
| EP-06 | `/api/auth/me` | GET | Happy Path | 200 + datos del usuario autenticado |
| EP-07 | `/api/auth/me` | GET | Acceso | 403 — Sin token |

### 3.2 Mesa de Ayuda — Tickets (14 tests)

| ID | Endpoint | Método | Categoría | Resultado Esperado |
|----|----------|--------|-----------|-------------------|
| EP-08 | `/api/tickets` | GET | Happy Path | 200 + array de tickets (ADMIN ve todos) |
| EP-09 | `/api/tickets` | GET | Acceso | 200 + array filtrado (TECNICO solo asignados) |
| EP-10 | `/api/tickets/my` | GET | Happy Path | 200 + tickets del OPERADOR |
| EP-11 | `/api/tickets/1` | GET | Happy Path | 200 + ticket con id=1, campos completos |
| EP-12 | `/api/tickets/9999` | GET | Error | 404 — Ticket inexistente |
| EP-13 | `/api/tickets` | POST | Happy Path | 201 + ticket creado, status=SOLICITADO |
| EP-14 | `/api/tickets` | POST | Regla Negocio | 201 + prioridad auto ALTA (asunto contiene "Juez") |
| EP-15 | `/api/tickets` | POST | Acceso | 403 — TECNICO no puede crear tickets |
| EP-16 | `/api/tickets/2/status` | PATCH | Flujo | 200 + SOLICITADO → ASIGNADO |
| EP-17 | `/api/tickets/2/status` | PATCH | Flujo | 200 + ASIGNADO → EN_CURSO |
| EP-18 | `/api/tickets/2/status` | PATCH | Flujo | 200 + EN_CURSO → CERRADO + closedAt |
| EP-19 | `/api/tickets/1/status` | PATCH | Error | 409 — Transición de estado inválida |
| EP-20 | `/api/tickets/1/status` | PATCH | Acceso | 403 — OPERADOR no puede cambiar estado |
| EP-21 | `/api/tickets/4` | DELETE | Happy Path | 204 — Soft delete exitoso |

### 3.3 Inventario — Hardware (7 tests)

| ID | Endpoint | Método | Categoría | Resultado Esperado |
|----|----------|--------|-----------|-------------------|
| EP-22 | `/api/inventory/hardware` | GET | Happy Path | 200 + array con equipos |
| EP-23 | `/api/inventory/hardware/1` | GET | Happy Path | 200 + equipo con inventarioPatrimonial |
| EP-24 | `/api/inventory/hardware` | POST | Happy Path | 201 + hardware creado |
| EP-25 | `/api/inventory/hardware` | POST | Error | 409 — Inventario patrimonial duplicado |
| EP-26 | `/api/inventory/hardware` | POST | Validación | 400 — Sin campo 'clase' obligatorio |
| EP-27 | `/api/inventory/hardware/1` | PUT | Happy Path | 200 + hardware actualizado |
| EP-28 | `/api/inventory/hardware/4` | DELETE | Happy Path | 204 — Soft delete |

### 3.4 Inventario — Software (6 tests)

| ID | Endpoint | Método | Categoría | Resultado Esperado |
|----|----------|--------|-----------|-------------------|
| EP-29 | `/api/inventory/software` | GET | Happy Path | 200 + array |
| EP-30 | `/api/inventory/software/1` | GET | Happy Path | 200 + software con nombre |
| EP-31 | `/api/inventory/software` | POST | Happy Path | 201 + software creado |
| EP-32 | `/api/inventory/software` | POST | Validación | 400 — Sin campo 'nombre' obligatorio |
| EP-33 | `/api/inventory/software/1` | PUT | Happy Path | 200 + actualizado |
| EP-34 | `/api/inventory/software/3` | DELETE | Happy Path | 204 — Soft delete |

### 3.5 Contratos (7 tests)

| ID | Endpoint | Método | Categoría | Resultado Esperado |
|----|----------|--------|-----------|-------------------|
| EP-35 | `/api/contracts` | GET | Happy Path | 200 + array de contratos activos |
| EP-36 | `/api/contracts/1` | GET | Happy Path | 200 + contrato con proveedor |
| EP-37 | `/api/contracts` | POST | Happy Path | 201 + contrato creado |
| EP-38 | `/api/contracts` | POST | Validación | 400 — Sin campo 'nombre' obligatorio |
| EP-39 | `/api/contracts/1` | PUT | Happy Path | 200 + actualizado |
| EP-40 | `/api/contracts/2` | DELETE | Happy Path | 204 — Desactivado |
| EP-41 | `/api/contracts/expiring?days=365` | GET | Happy Path | 200 + contratos próximos a vencer |

### 3.6 Estructura Territorial (3 tests)

| ID | Endpoint | Método | Categoría | Resultado Esperado |
|----|----------|--------|-----------|-------------------|
| EP-42 | `/api/locations/circunscripciones` | GET | Happy Path | 200 + array de circunscripciones |
| EP-43 | `/api/locations/juzgados` | GET | Happy Path | 200 + array de juzgados |
| EP-44 | `/api/locations/edificios/1/juzgados` | GET | Happy Path | 200 + juzgados del edificio |

### 3.7 Usuarios (9 tests)

| ID | Endpoint | Método | Categoría | Resultado Esperado |
|----|----------|--------|-----------|-------------------|
| EP-45 | `/api/users` | GET | Happy Path | 200 + array de usuarios |
| EP-46 | `/api/users` | GET | Acceso | 403 — TECNICO sin acceso |
| EP-47 | `/api/users/1` | GET | Happy Path | 200 + datos del usuario |
| EP-48 | `/api/users/role/TECNICO` | GET | Happy Path | 200 + filtrado por rol |
| EP-49 | `/api/users` | POST | Happy Path | 201 + usuario creado (sin password en respuesta) |
| EP-50 | `/api/users` | POST | Error | 409 — Username duplicado |
| EP-51 | `/api/users` | POST | Validación | 400 — Sin username obligatorio |
| EP-52 | `/api/users/1` | PUT | Happy Path | 200 + actualizado |
| EP-53 | `/api/users/2` | DELETE | Happy Path | 204 — Soft delete |

### 3.8 Auditoría (3 tests)

| ID | Endpoint | Método | Categoría | Resultado Esperado |
|----|----------|--------|-----------|-------------------|
| EP-54 | `/api/audit` | GET | Happy Path | 200 + array de registros |
| EP-55 | `/api/audit/entity/Ticket/1` | GET | Happy Path | 200 + historial de la entidad |
| EP-56 | `/api/audit` | GET | Acceso | 403 — TECNICO sin acceso |

### 3.9 Dashboard (1 test)

| ID | Endpoint | Método | Categoría | Resultado Esperado |
|----|----------|--------|-----------|-------------------|
| EP-57 | `/api/dashboard/stats` | GET | Happy Path | 200 + métricas del sistema |

---

## 4. Distribución por Categoría

| Categoría | Cantidad | Descripción |
|-----------|----------|-------------|
| **Happy Path** | 37 | Flujo normal exitoso |
| **Control de Acceso** | 7 | Permisos por rol (403) |
| **Validación** | 4 | Campos obligatorios (400) |
| **Error Controlado** | 6 | 404, 409 |
| **Regla de Negocio** | 1 | Prioridad automática |
| **Flujo Completo** | 2 | Ciclo de vida de ticket |
| **Total** | **57** | |

---

## 5. Criterios de Aceptación

| Criterio | Umbral |
|----------|--------|
| Tests pasados | **100%** (57/57) |
| Tiempo de respuesta | < 2000ms por endpoint |
| Cobertura de módulos | 9/9 módulos cubiertos |
| Cobertura de roles | 3/3 roles probados |
| Tipos de prueba | Happy path + Error + Acceso + Validación |

---

## 6. Instrucciones de Ejecución

### Prerrequisitos
1. Backend corriendo en `http://localhost:8080`
2. Postman instalado (v10+)

### Pasos
1. **Importar colección**: `File → Import → SOJUS_Fase2.postman_collection.json`
2. **Importar environment**: `File → Import → SOJUS_Environment.postman_environment.json`
3. **Seleccionar environment**: En la esquina superior derecha, seleccionar `SOJUS - Local`
4. **Ejecutar colección completa**:
   - Click derecho en la colección → `Run collection`
   - Mantener el orden predeterminado (Autenticación primero)
   - Click en `Run SOJUS HelpDesk Judicial`
5. **Revisar resultados**: Todas las pruebas deben mostrar ✅ verde

### Ejecución Secuencial Obligatoria
> **IMPORTANTE**: La carpeta "1. Autenticación" debe ejecutarse primero para obtener los tokens JWT que usan el resto de las pruebas.

---

## 7. Artefactos

| Archivo | Descripción |
|---------|-------------|
| `SOJUS_Fase2.postman_collection.json` | Colección con 57 endpoints y test scripts |
| `SOJUS_Environment.postman_environment.json` | Variables de entorno |
| `plan_de_pruebas_backend.md` | Este documento |
