-- ============================================================
-- SOJUS — Datos Semilla para Docker (PostgreSQL)
-- Este script se ejecuta automáticamente cuando PostgreSQL
-- crea la base de datos por primera vez.
-- ALINEADO con DataInitializer.java para consistencia.
-- ============================================================

-- ============================================================
-- 1. ESTRUCTURA DE TABLAS
-- Las tablas son creadas por Hibernate (ddl-auto=update),
-- pero las definimos aquí como respaldo por si este script
-- se ejecuta antes de que Hibernate inicie.
-- ============================================================

CREATE TABLE IF NOT EXISTS circunscripciones (
    id          BIGSERIAL       PRIMARY KEY,
    nombre      VARCHAR(100)    NOT NULL,
    codigo      VARCHAR(20),
    created_at  TIMESTAMP       NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS distritos (
    id                  BIGSERIAL       PRIMARY KEY,
    nombre              VARCHAR(100)    NOT NULL,
    ciudad              VARCHAR(100),
    circunscripcion_id  BIGINT          NOT NULL,
    created_at          TIMESTAMP       NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_distrito_circunscripcion
        FOREIGN KEY (circunscripcion_id) REFERENCES circunscripciones (id)
);

CREATE TABLE IF NOT EXISTS edificios (
    id          BIGSERIAL       PRIMARY KEY,
    nombre      VARCHAR(150)    NOT NULL,
    direccion   VARCHAR(200),
    distrito_id BIGINT          NOT NULL,
    created_at  TIMESTAMP       NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_edificio_distrito
        FOREIGN KEY (distrito_id) REFERENCES distritos (id)
);

CREATE TABLE IF NOT EXISTS juzgados (
    id          BIGSERIAL       PRIMARY KEY,
    nombre      VARCHAR(200)    NOT NULL,
    fuero       VARCHAR(50),
    secretaria  VARCHAR(100),
    edificio_id BIGINT          NOT NULL,
    active      BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP       NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_juzgado_edificio
        FOREIGN KEY (edificio_id) REFERENCES edificios (id)
);

CREATE TABLE IF NOT EXISTS users (
    id          BIGSERIAL       PRIMARY KEY,
    username    VARCHAR(50)     NOT NULL UNIQUE,
    password    VARCHAR(255)    NOT NULL,
    full_name   VARCHAR(100)    NOT NULL,
    email       VARCHAR(150),
    role        VARCHAR(30)     NOT NULL,
    juzgado_id  BIGINT,
    active      BOOLEAN         NOT NULL DEFAULT TRUE,
    deleted     BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP,
    CONSTRAINT fk_user_juzgado
        FOREIGN KEY (juzgado_id) REFERENCES juzgados (id)
);

CREATE TABLE IF NOT EXISTS hardware (
    id                      BIGSERIAL       PRIMARY KEY,
    inventario_patrimonial  VARCHAR(30)     NOT NULL UNIQUE,
    numero_serie            VARCHAR(50),
    clase                   VARCHAR(50)     NOT NULL,
    tipo                    VARCHAR(50),
    marca                   VARCHAR(80),
    modelo                  VARCHAR(100),
    estado                  VARCHAR(20)     NOT NULL DEFAULT 'ACTIVO',
    juzgado_id              BIGINT,
    ubicacion_fisica        VARCHAR(100),
    observaciones           TEXT,
    created_at              TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMP,
    deleted                 BOOLEAN         NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_hardware_juzgado
        FOREIGN KEY (juzgado_id) REFERENCES juzgados (id)
);

CREATE TABLE IF NOT EXISTS software (
    id                  BIGSERIAL       PRIMARY KEY,
    nombre              VARCHAR(150)    NOT NULL,
    version             VARCHAR(50),
    fabricante          VARCHAR(100),
    tipo_licencia       VARCHAR(80),
    numero_licencia     VARCHAR(100),
    cantidad_licencias  INTEGER,
    fecha_vencimiento   DATE,
    estado              VARCHAR(20)     NOT NULL DEFAULT 'ACTIVO',
    observaciones       TEXT,
    created_at          TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP,
    deleted             BOOLEAN         NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS tickets (
    id                  BIGSERIAL       PRIMARY KEY,
    asunto              VARCHAR(200)    NOT NULL,
    descripcion         TEXT,
    status              VARCHAR(20)     NOT NULL DEFAULT 'SOLICITADO',
    prioridad           VARCHAR(10)     NOT NULL DEFAULT 'MEDIA',
    juzgado_id          BIGINT,
    solicitante_id      BIGINT,
    tecnico_asignado_id BIGINT,
    hardware_id         BIGINT,
    bitacora            TEXT,
    canal               VARCHAR(50),
    created_at          TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP,
    closed_at           TIMESTAMP,
    deleted             BOOLEAN         NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_ticket_juzgado FOREIGN KEY (juzgado_id) REFERENCES juzgados (id),
    CONSTRAINT fk_ticket_solicitante FOREIGN KEY (solicitante_id) REFERENCES users (id),
    CONSTRAINT fk_ticket_tecnico FOREIGN KEY (tecnico_asignado_id) REFERENCES users (id),
    CONSTRAINT fk_ticket_hardware FOREIGN KEY (hardware_id) REFERENCES hardware (id)
);

CREATE TABLE IF NOT EXISTS contracts (
    id              BIGSERIAL       PRIMARY KEY,
    nombre          VARCHAR(200)    NOT NULL,
    proveedor       VARCHAR(150)    NOT NULL,
    numero_contrato VARCHAR(50),
    fecha_inicio    DATE,
    fecha_fin       DATE,
    cobertura_hw    VARCHAR(200),
    cobertura_sw    VARCHAR(200),
    sla_descripcion TEXT,
    observaciones   TEXT,
    active          BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP
);

CREATE TABLE IF NOT EXISTS audit_log (
    id          BIGSERIAL       PRIMARY KEY,
    entity_name VARCHAR(100)    NOT NULL,
    entity_id   BIGINT,
    action      VARCHAR(30)     NOT NULL,
    username    VARCHAR(50),
    old_value   TEXT,
    new_value   TEXT,
    field       VARCHAR(100),
    timestamp   TIMESTAMP       NOT NULL DEFAULT NOW()
);

-- ============================================================
-- 2. DATOS SEMILLA (alineados con DataInitializer.java)
-- ============================================================

-- Circunscripciones (DataInitializer crea 2)
INSERT INTO circunscripciones (id, nombre, codigo) VALUES
    (1, 'Primera Circunscripción',  'CIRC-001'),
    (2, 'Segunda Circunscripción',  'CIRC-002')
ON CONFLICT (id) DO NOTHING;

-- Distritos (DataInitializer crea 2)
INSERT INTO distritos (id, nombre, ciudad, circunscripcion_id) VALUES
    (1, 'Santa Fe', 'Santa Fe', 1),
    (2, 'Rosario',  'Rosario',  2)
ON CONFLICT (id) DO NOTHING;

-- Edificios (DataInitializer crea 2)
INSERT INTO edificios (id, nombre, direccion, distrito_id) VALUES
    (1, 'Tribunales Santa Fe', '1° de Mayo 2551',  1),
    (2, 'Tribunales Rosario',  'Balcarce 1651',    2)
ON CONFLICT (id) DO NOTHING;

-- Juzgados (DataInitializer crea 3)
INSERT INTO juzgados (id, nombre, fuero, secretaria, edificio_id) VALUES
    (1, 'Juzgado Civil y Comercial Nro 1', 'Civil',    'Secretaría 1',     1),
    (2, 'Juzgado Penal Nro 3',             'Penal',    'Secretaría 1',     2),
    (3, 'Juzgado Laboral Nro 2',           'Laboral',  'Secretaría Única', 1)
ON CONFLICT (id) DO NOTHING;

-- Usuarios (DataInitializer crea 3)
-- Passwords: admin → admin123 | operador → oper123 | tecnico → tec123
INSERT INTO users (id, username, password, full_name, email, role, juzgado_id) VALUES
    (1, 'admin',    '$2a$10$HURzhrjYCBjP6w7rzw2y2Or.oEzAYzQkaOBqOmJl7EsCpJSD.znfq', 'María García (Admin)',      'admin@poderjudicial.gov.ar',    'ADMINISTRADOR', 1),
    (2, 'operador', '$2a$10$3GA2PAnBIA8RXtUp0.oZRuYae5bvRZ8XETvTRQ0YnjYtZcDuDmk1W', 'Carlos López (Operador)',   'operador@poderjudicial.gov.ar', 'OPERADOR',      1),
    (3, 'tecnico',  '$2a$10$SU7eMndiW8QS6WCfpbX/fOf6cucRUu5UwSy2wjtFIb5hRUgR.8F9W', 'Ana Martínez (Técnica)',    'tecnico@poderjudicial.gov.ar',  'TECNICO',       NULL)
ON CONFLICT (username) DO NOTHING;

-- Hardware (DataInitializer crea 4)
INSERT INTO hardware (id, inventario_patrimonial, numero_serie, clase, tipo, marca, modelo, estado, juzgado_id, ubicacion_fisica) VALUES
    (1, 'INV-001-0001', 'SN-DELL-001',     'PC',        'Desktop',   'Dell',  'OptiPlex 7090',           'ACTIVO', 1,    'Puesto Secretario'),
    (2, 'INV-001-0002', 'SN-HP-002',       'PC',        'All-in-One','HP',    'ProOne 440 G9',           'ACTIVO', 2,    'Puesto Juez'),
    (3, 'INV-002-0001', 'SN-EPSON-001',    'Impresora', 'Láser',     'Epson', 'WorkForce Pro WF-C5790',  'ACTIVO', 1,    'Mesa Compartida'),
    (4, 'INV-003-0001', 'SN-DELL-SRV-001', 'Servidor',  'Rack',      'Dell',  'PowerEdge R750',          'ACTIVO', NULL, 'Data Center - Rack 3')
ON CONFLICT (inventario_patrimonial) DO NOTHING;

-- Software (DataInitializer crea 3)
INSERT INTO software (id, nombre, version, fabricante, tipo_licencia, cantidad_licencias, fecha_vencimiento, estado) VALUES
    (1, 'Microsoft Office 365',    '2024', 'Microsoft', 'Suscripción Anual', 500, '2026-12-31', 'ACTIVO'),
    (2, 'Antivirus ESET Endpoint', '10.1', 'ESET',      'Corporativa',       800, '2026-06-30', 'ACTIVO'),
    (3, 'Sistema LEX Doctor',      '12.0', 'LEX Doctor', 'Perpetua',          200, NULL,          'ACTIVO')
ON CONFLICT (id) DO NOTHING;

-- Contratos (DataInitializer crea 3)
INSERT INTO contracts (id, nombre, proveedor, numero_contrato, fecha_inicio, fecha_fin, cobertura_hw, cobertura_sw, sla_descripcion) VALUES
    (1, 'Soporte HW Dell',         'Dell Argentina S.A.', 'CNT-2024-001', '2024-01-01', '2026-12-31', 'PCs y Servidores Dell',           NULL,                              'Respuesta 4hs hábiles, resolución 24hs'),
    (2, 'Mantenimiento Impresoras', 'Tecno Print SRL',     'CNT-2024-002', '2024-03-01', '2026-03-15', 'Impresoras Epson y HP',           NULL,                              'Visita técnica en 48hs'),
    (3, 'Licencias Microsoft EA',   'Microsoft Corp.',     'CNT-2024-003', '2024-01-01', '2026-04-01', NULL,                              'Office 365, Windows, Azure AD',   'Mesa de ayuda 24/7')
ON CONFLICT (id) DO NOTHING;

-- Tickets (DataInitializer crea 4: solicitante=operador(2), técnico=tecnico(3))
INSERT INTO tickets (id, asunto, descripcion, status, prioridad, juzgado_id, solicitante_id, tecnico_asignado_id, hardware_id, bitacora, canal) VALUES
    (1, 'Impresora no funciona en Secretaría',
        'La impresora del puesto del Secretario no enciende desde ayer.',
        'SOLICITADO', 'MEDIA', 1, 2, NULL, 3,
        NULL, 'WEB'),
    (2, 'PC del Juez no inicia - Sala de Audiencias',
        'La PC del Juez en la Sala de Audiencias no enciende. URGENTE.',
        'SOLICITADO', 'ALTA', 2, 2, NULL, 2,
        NULL, 'WEB'),
    (3, 'Solicitar tóner para impresora',
        'Se necesita cambio de tóner en la impresora de Mesa Compartida.',
        'EN_CURSO', 'BAJA', 1, 2, 3, NULL,
        NULL, 'PORTAL'),
    (4, 'Instalación de LEX Doctor en nueva PC',
        'Instalar LEX Doctor 12.0 en la nueva PC del Juzgado Laboral.',
        'CERRADO', 'MEDIA', 3, 2, 3, NULL,
        E'[2026-02-20 10:00] operador: Solicitud de instalación\n[2026-02-21 14:30] tecnico: Instalación completada\n', 'WEB')
ON CONFLICT (id) DO NOTHING;

-- Reset sequences para que los próximos INSERTs usen IDs consecutivos
SELECT setval('circunscripciones_id_seq', (SELECT MAX(id) FROM circunscripciones));
SELECT setval('distritos_id_seq',         (SELECT MAX(id) FROM distritos));
SELECT setval('edificios_id_seq',         (SELECT MAX(id) FROM edificios));
SELECT setval('juzgados_id_seq',          (SELECT MAX(id) FROM juzgados));
SELECT setval('users_id_seq',             (SELECT MAX(id) FROM users));
SELECT setval('hardware_id_seq',          (SELECT MAX(id) FROM hardware));
SELECT setval('software_id_seq',          (SELECT MAX(id) FROM software));
SELECT setval('contracts_id_seq',         (SELECT MAX(id) FROM contracts));
SELECT setval('tickets_id_seq',           (SELECT MAX(id) FROM tickets));
