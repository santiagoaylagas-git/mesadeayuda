-- ============================================================
-- SOJUS — Datos Semilla para Docker
-- Este script se ejecuta automáticamente cuando PostgreSQL
-- crea la base de datos por primera vez.
-- Las tablas son creadas por Hibernate (ddl-auto=update).
-- ============================================================

-- NOTA: Este script se ejecuta ANTES de que Hibernate cree las tablas.
-- Por eso usamos CREATE TABLE IF NOT EXISTS para las tablas necesarias,
-- y luego insertamos los datos semilla.

-- ============================================================
-- 1. ESTRUCTURA TERRITORIAL
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
    entity_name VARCHAR(50)     NOT NULL,
    entity_id   BIGINT,
    action      VARCHAR(30)     NOT NULL,
    username    VARCHAR(50),
    old_value   TEXT,
    new_value   TEXT,
    field       VARCHAR(100),
    timestamp   TIMESTAMP       NOT NULL DEFAULT NOW()
);

-- ============================================================
-- DATOS SEMILLA
-- ============================================================

-- Circunscripciones
INSERT INTO circunscripciones (nombre, codigo) VALUES
    ('Primera Circunscripción',  'CIRC-001'),
    ('Segunda Circunscripción',  'CIRC-002'),
    ('Tercera Circunscripción',  'CIRC-003'),
    ('Cuarta Circunscripción',   'CIRC-004'),
    ('Quinta Circunscripción',   'CIRC-005')
ON CONFLICT DO NOTHING;

-- Distritos
INSERT INTO distritos (nombre, ciudad, circunscripcion_id) VALUES
    ('Distrito Santa Fe',     'Santa Fe',      1),
    ('Distrito Rosario',      'Rosario',       2),
    ('Distrito Venado Tuerto','Venado Tuerto', 3),
    ('Distrito Reconquista',  'Reconquista',   4),
    ('Distrito Rafaela',      'Rafaela',       5)
ON CONFLICT DO NOTHING;

-- Edificios
INSERT INTO edificios (nombre, direccion, distrito_id) VALUES
    ('Tribunales Santa Fe',         'Av. Gral. López 2550, Santa Fe',      1),
    ('Centro de Justicia Penal SF', '1ra. Junta 2651, Santa Fe',           1),
    ('Tribunales Rosario',          'Balcarce 1651, Rosario',              2),
    ('Centro de Justicia Penal Ros','Salta 2350, Rosario',                 2),
    ('Juzgado Venado Tuerto',       'Belgrano 321, Venado Tuerto',         3)
ON CONFLICT DO NOTHING;

-- Juzgados
INSERT INTO juzgados (nombre, fuero, secretaria, edificio_id) VALUES
    ('Juzgado Civil y Comercial N°1',   'Civil',    'Secretaría N°1', 1),
    ('Juzgado Civil y Comercial N°2',   'Civil',    'Secretaría N°2', 1),
    ('Juzgado Penal N°1',               'Penal',    'Secretaría N°1', 2),
    ('Juzgado Laboral N°1',             'Laboral',  'Secretaría N°1', 3),
    ('Juzgado de Familia N°1',          'Familia',  'Secretaría N°1', 3),
    ('Juzgado Penal N°2',               'Penal',    'Secretaría N°2', 4),
    ('Juzgado Civil Venado Tuerto',     'Civil',    'Secretaría N°1', 5)
ON CONFLICT DO NOTHING;

-- Usuarios (contraseñas alineadas con DataInitializer y README)
-- admin → admin123 | operador → oper123 | tecnico → tec123
INSERT INTO users (username, password, full_name, email, role, juzgado_id) VALUES
    ('admin',    '$2a$10$RAgIHj9UR0/rpPSb823laub.t.ipUgmaNO/V.z/ki60nn9X97vqdG', 'María García (Admin)',   'admin@poderjudicial.gov.ar',    'ADMINISTRADOR', 1),
    ('operador', '$2a$10$dk8BGferFMB7t.ojWi9Wn.z2dxn8Oxl/lupmxp4jrD0n0d11.t5re', 'Carlos López (Operador)','operador@poderjudicial.gov.ar', 'OPERADOR',      1),
    ('tecnico',  '$2a$10$yYsd7Hnv6/ReaqktyFL0A.3.XGzCzAEw9WapS/kPCtvACtDdWmb3K', 'Ana Martínez (Técnica)', 'tecnico@poderjudicial.gov.ar',  'TECNICO',       NULL)
ON CONFLICT (username) DO NOTHING;

-- Hardware
INSERT INTO hardware (inventario_patrimonial, numero_serie, clase, tipo, marca, modelo, estado, juzgado_id, ubicacion_fisica) VALUES
    ('INV-001-0001', 'SN-ABC-001', 'PC',        'Desktop',     'Lenovo',  'ThinkCentre M720',  'ACTIVO',        1, 'Puesto 1 - Secretaría'),
    ('INV-001-0002', 'SN-ABC-002', 'PC',        'All-in-One',  'HP',      'ProOne 440 G6',     'ACTIVO',        1, 'Puesto 2 - Despacho Juez'),
    ('INV-001-0003', 'SN-ABC-003', 'Impresora', 'Láser',       'Brother', 'HL-L2360DW',        'ACTIVO',        2, 'Área común'),
    ('INV-002-0001', 'SN-DEF-001', 'Servidor',  'Rack',        'Dell',    'PowerEdge R740',    'ACTIVO',        NULL, 'Datacenter Principal'),
    ('INV-003-0001', 'SN-GHI-001', 'PC',        'Notebook',    'Lenovo',  'ThinkPad T490',     'EN_REPARACION', 3, 'Puesto 3 - Sala Audiencias')
ON CONFLICT (inventario_patrimonial) DO NOTHING;

-- Software
INSERT INTO software (nombre, version, fabricante, tipo_licencia, numero_licencia, cantidad_licencias, fecha_vencimiento, estado) VALUES
    ('Microsoft Office 365',    '2024',  'Microsoft',     'Suscripción', 'LIC-MS-001', 150, '2027-03-31', 'ACTIVO'),
    ('Windows 11 Pro',          '23H2',  'Microsoft',     'OEM',         'LIC-MS-002', 200, NULL,          'ACTIVO'),
    ('Antivirus Kaspersky',     'v21',   'Kaspersky Lab', 'Anual',       'LIC-KS-001', 200, '2026-12-31', 'ACTIVO'),
    ('Sistema Lex Doctor',      '12.0',  'Lex Doctor',    'Perpetua',    'LIC-LD-001', 50,  NULL,          'ACTIVO'),
    ('Adobe Acrobat Pro',       '2024',  'Adobe',         'Suscripción', 'LIC-AD-001', 30,  '2026-06-30', 'ACTIVO')
ON CONFLICT DO NOTHING;

-- Contratos
INSERT INTO contracts (nombre, proveedor, numero_contrato, fecha_inicio, fecha_fin, cobertura_hw, cobertura_sw, sla_descripcion) VALUES
    ('Mantenimiento de PCs',   'TechCorp SRL',    'CONT-2025-001', '2025-01-01', '2026-12-31', 'PCs de escritorio y notebooks', NULL, 'Respuesta en 4 horas hábiles. Resolución en 24 horas.'),
    ('Soporte de Servidores',  'DataCenter SA',    'CONT-2025-002', '2025-03-01', '2026-03-01', 'Servidores Dell PowerEdge',     NULL, 'Soporte 24/7. Resolución en 4 horas.'),
    ('Licencias Microsoft EA', 'Microsoft',        'CONT-MS-2025',  '2025-01-01', '2027-12-31', NULL, 'Office 365, Windows, Server CALs', 'Renovación anual automática.'),
    ('Soporte Impresoras',     'PrintServices SA', 'CONT-2025-004', '2025-06-01', '2026-06-01', 'Impresoras Brother y HP', NULL, 'Respuesta en 8 horas hábiles. Incluye tóner.')
ON CONFLICT DO NOTHING;

-- Tickets (solicitante=operador(2), técnico=tecnico(3))
INSERT INTO tickets (asunto, descripcion, status, prioridad, juzgado_id, solicitante_id, tecnico_asignado_id, hardware_id, bitacora, canal) VALUES
    ('PC no enciende',                  'La PC del puesto 1 no enciende desde esta mañana.',                 'ASIGNADO',   'ALTA',  1, 2, 3, 1, 'Ticket creado por operador - Asignado a tecnico', 'WEB'),
    ('Instalar Office en nuevo equipo', 'Se requiere instalación de Office 365 en equipo recién recibido.',  'SOLICITADO', 'MEDIA', 2, 2, NULL, NULL, 'Ticket creado por operador', 'WEB'),
    ('Impresora atascada',              'La impresora del área común se atasca constantemente.',              'EN_CURSO',   'MEDIA', 2, 2, 3, 3, 'Ticket creado - Técnico en camino', 'PORTAL'),
    ('Sin acceso a internet',           'No hay conectividad en todo el juzgado desde las 14:00.',           'SOLICITADO', 'ALTA',  1, 2, NULL, NULL, 'Ticket creado por operador', 'EMAIL'),
    ('Solicitar tóner',                 'Se agotó el tóner de la impresora Brother del piso 2.',             'CERRADO',    'BAJA',  1, 2, 3, 3, 'Solicitado - Tóner reemplazado. Cerrado.', 'PORTAL')
ON CONFLICT DO NOTHING;
