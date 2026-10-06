-- ==============================================================================================
-- SISTEMA ERP - NICARAGUA
-- ARCHIVO: modulo1_seguridad.sql
-- DESCRIPCIÓN: Script DDL y Seed Data para el Módulo 1 (Seguridad).
-- POR QUÉ EXISTE: Define las tablas núcleo de autenticación, autorización basada en roles y 
--                 pantallas, y establece los datos maestros esenciales (Roles, Permisos, Pantallas,
--                 Matriz de Autorizaciones y Usuario Administrador inicial).
-- CONEXIÓN CON EL RESTO: Provee la base de auditoría y seguridad sobre la cual se soportarán
--                        todos los módulos del ERP en las Fases 1 a 4.
-- ==============================================================================================

USE SistemaERP;
GO

-- 1. TABLA: Roles
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'Roles')
BEGIN
    CREATE TABLE Roles (
        RolID INT IDENTITY(1,1) NOT NULL,
        Nombre VARCHAR(50) NOT NULL,
        Descripcion VARCHAR(255) NULL,
        Activo BIT NOT NULL DEFAULT 1,
        FechaEliminacion DATETIME NULL,
        CreadoPor VARCHAR(50) NOT NULL DEFAULT 'SYSTEM',
        FechaCreacion DATETIME NOT NULL DEFAULT GETDATE(),
        ModificadoPor VARCHAR(50) NULL,
        FechaModificacion DATETIME NULL,
        CONSTRAINT PK_Roles PRIMARY KEY CLUSTERED (RolID),
        CONSTRAINT UQ_Roles_Nombre UNIQUE (Nombre)
    );
END
GO

-- 2. TABLA: Usuarios
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'Usuarios')
BEGIN
    CREATE TABLE Usuarios (
        UsuarioID INT IDENTITY(1,1) NOT NULL,
        Username VARCHAR(50) NOT NULL,
        PasswordHash VARCHAR(255) NOT NULL,
        NombreCompleto VARCHAR(150) NOT NULL,
        Email VARCHAR(100) NOT NULL,
        Telefono VARCHAR(30) NULL,
        RolID INT NOT NULL,
        Activo BIT NOT NULL DEFAULT 1,
        FechaEliminacion DATETIME NULL,
        CreadoPor VARCHAR(50) NOT NULL DEFAULT 'SYSTEM',
        FechaCreacion DATETIME NOT NULL DEFAULT GETDATE(),
        ModificadoPor VARCHAR(50) NULL,
        FechaModificacion DATETIME NULL,
        CONSTRAINT PK_Usuarios PRIMARY KEY CLUSTERED (UsuarioID),
        CONSTRAINT UQ_Usuarios_Username UNIQUE (Username),
        CONSTRAINT UQ_Usuarios_Email UNIQUE (Email),
        CONSTRAINT FK_Usuarios_Roles FOREIGN KEY (RolID) REFERENCES Roles(RolID)
    );
END
GO

-- 3. TABLA: Pantallas
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'Pantallas')
BEGIN
    CREATE TABLE Pantallas (
        PantallaID INT IDENTITY(1,1) NOT NULL,
        Codigo VARCHAR(50) NOT NULL,
        Nombre VARCHAR(100) NOT NULL,
        Ruta VARCHAR(100) NOT NULL,
        Icono VARCHAR(50) NULL,
        Modulo VARCHAR(50) NOT NULL,
        Orden INT NOT NULL DEFAULT 1,
        Activo BIT NOT NULL DEFAULT 1,
        FechaEliminacion DATETIME NULL,
        CreadoPor VARCHAR(50) NOT NULL DEFAULT 'SYSTEM',
        FechaCreacion DATETIME NOT NULL DEFAULT GETDATE(),
        ModificadoPor VARCHAR(50) NULL,
        FechaModificacion DATETIME NULL,
        CONSTRAINT PK_Pantallas PRIMARY KEY CLUSTERED (PantallaID),
        CONSTRAINT UQ_Pantallas_Codigo UNIQUE (Codigo)
    );
END
GO

-- 4. TABLA: Permisos
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'Permisos')
BEGIN
    CREATE TABLE Permisos (
        PermisoID INT IDENTITY(1,1) NOT NULL,
        Codigo VARCHAR(50) NOT NULL,
        Nombre VARCHAR(100) NOT NULL,
        Descripcion VARCHAR(255) NULL,
        Activo BIT NOT NULL DEFAULT 1,
        FechaEliminacion DATETIME NULL,
        CreadoPor VARCHAR(50) NOT NULL DEFAULT 'SYSTEM',
        FechaCreacion DATETIME NOT NULL DEFAULT GETDATE(),
        ModificadoPor VARCHAR(50) NULL,
        FechaModificacion DATETIME NULL,
        CONSTRAINT PK_Permisos PRIMARY KEY CLUSTERED (PermisoID),
        CONSTRAINT UQ_Permisos_Codigo UNIQUE (Codigo)
    );
END
GO

-- 5. TABLA: RolesPermisos (Matriz de Control de Acceso por Pantalla y Acción)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'RolesPermisos')
BEGIN
    CREATE TABLE RolesPermisos (
        RolPermisoID INT IDENTITY(1,1) NOT NULL,
        RolID INT NOT NULL,
        PantallaID INT NOT NULL,
        PermisoID INT NOT NULL,
        Activo BIT NOT NULL DEFAULT 1,
        FechaEliminacion DATETIME NULL,
        CreadoPor VARCHAR(50) NOT NULL DEFAULT 'SYSTEM',
        FechaCreacion DATETIME NOT NULL DEFAULT GETDATE(),
        ModificadoPor VARCHAR(50) NULL,
        FechaModificacion DATETIME NULL,
        CONSTRAINT PK_RolesPermisos PRIMARY KEY CLUSTERED (RolPermisoID),
        CONSTRAINT FK_RolesPermisos_Roles FOREIGN KEY (RolID) REFERENCES Roles(RolID),
        CONSTRAINT FK_RolesPermisos_Pantallas FOREIGN KEY (PantallaID) REFERENCES Pantallas(PantallaID),
        CONSTRAINT FK_RolesPermisos_Permisos FOREIGN KEY (PermisoID) REFERENCES Permisos(PermisoID),
        CONSTRAINT UQ_RolesPermisos UNIQUE (RolID, PantallaID, PermisoID)
    );
END
GO

-- ÍNDICES DE RENDIMIENTO
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'IX_Usuarios_RolID')
    CREATE NONCLUSTERED INDEX IX_Usuarios_RolID ON Usuarios(RolID);
GO

IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'IX_Pantallas_Modulo_Orden')
    CREATE NONCLUSTERED INDEX IX_Pantallas_Modulo_Orden ON Pantallas(Modulo, Orden);
GO

IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'IX_RolesPermisos_RolID')
    CREATE NONCLUSTERED INDEX IX_RolesPermisos_RolID ON RolesPermisos(RolID);
GO

IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'IX_RolesPermisos_PantallaID')
    CREATE NONCLUSTERED INDEX IX_RolesPermisos_PantallaID ON RolesPermisos(PantallaID);
GO

-- ==============================================================================================
-- SEED DATA: DATOS INICIALES DEL SISTEMA
-- ==============================================================================================

-- A. Inserción de Roles Obligatorios
IF NOT EXISTS (SELECT 1 FROM Roles WHERE Nombre = 'Administrador')
    INSERT INTO Roles (Nombre, Descripcion, CreadoPor) VALUES ('Administrador', 'Acceso total y administración integral del sistema', 'SEED');

IF NOT EXISTS (SELECT 1 FROM Roles WHERE Nombre = 'Gerente')
    INSERT INTO Roles (Nombre, Descripcion, CreadoPor) VALUES ('Gerente', 'Gestión operativa y financiera global sin acceso a configuración técnica', 'SEED');

IF NOT EXISTS (SELECT 1 FROM Roles WHERE Nombre = 'Vendedor')
    INSERT INTO Roles (Nombre, Descripcion, CreadoPor) VALUES ('Vendedor', 'Atención a clientes, facturación y consulta de existencias', 'SEED');

IF NOT EXISTS (SELECT 1 FROM Roles WHERE Nombre = 'Contador')
    INSERT INTO Roles (Nombre, Descripcion, CreadoPor) VALUES ('Contador', 'Gestión de contabilidad, cuentas por cobrar/pagar y reportería financiera', 'SEED');

IF NOT EXISTS (SELECT 1 FROM Roles WHERE Nombre = 'RH')
    INSERT INTO Roles (Nombre, Descripcion, CreadoPor) VALUES ('RH', 'Gestión de recursos humanos, expedientes de empleados y nómina', 'SEED');

IF NOT EXISTS (SELECT 1 FROM Roles WHERE Nombre = 'Consulta')
    INSERT INTO Roles (Nombre, Descripcion, CreadoPor) VALUES ('Consulta', 'Acceso de solo lectura para auditoría y visualización', 'SEED');
GO

-- B. Inserción de Permisos Estándar
IF NOT EXISTS (SELECT 1 FROM Permisos WHERE Codigo = 'ACCESO')
    INSERT INTO Permisos (Codigo, Nombre, Descripcion, CreadoPor) VALUES ('ACCESO', 'Acceso a Pantalla', 'Permite visualizar la pantalla y su información base', 'SEED');

IF NOT EXISTS (SELECT 1 FROM Permisos WHERE Codigo = 'CREAR')
    INSERT INTO Permisos (Codigo, Nombre, Descripcion, CreadoPor) VALUES ('CREAR', 'Creación de Registros', 'Permite registrar nuevas entidades o transacciones', 'SEED');

IF NOT EXISTS (SELECT 1 FROM Permisos WHERE Codigo = 'EDITAR')
    INSERT INTO Permisos (Codigo, Nombre, Descripcion, CreadoPor) VALUES ('EDITAR', 'Modificación de Registros', 'Permite actualizar datos existentes', 'SEED');

IF NOT EXISTS (SELECT 1 FROM Permisos WHERE Codigo = 'ELIMINAR')
    INSERT INTO Permisos (Codigo, Nombre, Descripcion, CreadoPor) VALUES ('ELIMINAR', 'Eliminación Lógica', 'Permite desactivar o realizar soft delete', 'SEED');

IF NOT EXISTS (SELECT 1 FROM Permisos WHERE Codigo = 'ANULAR')
    INSERT INTO Permisos (Codigo, Nombre, Descripcion, CreadoPor) VALUES ('ANULAR', 'Anulación de Transacciones', 'Permite anular facturas, notas de crédito o recibos oficiales', 'SEED');

IF NOT EXISTS (SELECT 1 FROM Permisos WHERE Codigo = 'IMPRIMIR')
    INSERT INTO Permisos (Codigo, Nombre, Descripcion, CreadoPor) VALUES ('IMPRIMIR', 'Impresión y Exportación', 'Permite emitir comprobantes POS, Carta y exportar reportes', 'SEED');
GO

-- C. Inserción del Catálogo Completo de Pantallas (21 Módulos - 4 Fases)
MERGE Pantallas AS target
USING (VALUES
    -- FASE 1: NÚCLEO
    ('SEG_USUARIOS',      'Usuarios del Sistema',         '/seguridad/usuarios',        'fa-users',                'Seguridad',   1),
    ('SEG_ROLES',         'Roles y Permisos',             '/seguridad/roles',           'fa-user-shield',          'Seguridad',   2),
    ('CLI_CLIENTES',      'Clientes',                     '/ventas/clientes',           'fa-address-book',         'Ventas',      1),
    ('FAC_FACTURACION',   'Facturación y Ventas',         '/ventas/facturacion',        'fa-file-invoice-dollar',  'Ventas',      2),
    ('FAC_NOTAS_CREDITO', 'Notas de Crédito',             '/ventas/notas-credito',      'fa-file-invoice',         'Ventas',      3),
    ('FAC_CAJAS',         'Cajas y Turnos',               '/ventas/cajas',              'fa-cash-register',        'Ventas',      4),
    ('CFG_CORRELATIVOS',  'Configuración Correlativos',   '/ventas/correlativos',       'fa-hashtag',              'Ventas',      5),
    ('PRV_PROVEEDORES',   'Proveedores',                  '/compras/proveedores',       'fa-truck',                'Compras',     1),
    ('COM_ORDENES',       'Órdenes de Compra',            '/compras/ordenes',           'fa-cart-shopping',        'Compras',     2),
    ('COM_RECEPCIONES',   'Recepciones de Mercancía',     '/compras/recepciones',       'fa-boxes-packing',        'Compras',     3),
    ('INV_PRODUCTOS',     'Productos y Categorías',       '/inventario/productos',      'fa-boxes-stacked',        'Inventario',  1),
    ('INV_KARDEX',        'Kardex y Movimientos',         '/inventario/kardex',         'fa-clipboard-list',       'Inventario',  2),
    -- FASE 2: FINANCIERO
    ('FIN_CXC',           'Cuentas por Cobrar (CxC)',     '/finanzas/cxc',              'fa-hand-holding-dollar',  'Finanzas',    1),
    ('FIN_CXP',           'Cuentas por Pagar (CxP)',      '/finanzas/cxp',              'fa-money-bill-wave',      'Finanzas',    2),
    ('FIN_BANCOS',        'Gestión de Bancos',            '/finanzas/bancos',           'fa-building-columns',     'Finanzas',    3),
    ('FIN_CAJA_CHICA',    'Caja Chica',                   '/finanzas/caja-chica',       'fa-wallet',               'Finanzas',    4),
    ('FIN_CONTABILIDAD',  'Contabilidad y Asientos',      '/finanzas/contabilidad',     'fa-book-bookmark',        'Finanzas',    5),
    -- FASE 3: RECURSOS HUMANOS
    ('RH_EMPLEADOS',      'Expediente de Empleados',      '/rh/empleados',              'fa-user-tie',             'RH',          1),
    ('RH_NOMINA',         'Nómina y Salarios',            '/rh/nomina',                 'fa-receipt',              'RH',          2),
    ('RH_VACACIONES',     'Vacaciones y Ausencias',       '/rh/vacaciones',             'fa-calendar-check',       'RH',          3),
    -- FASE 4: BI
    ('BI_DASHBOARDS',     'Dashboards e Indicadores',     '/bi/dashboards',             'fa-chart-pie',            'BI',          1)
) AS source (Codigo, Nombre, Ruta, Icono, Modulo, Orden)
ON target.Codigo = source.Codigo
WHEN NOT MATCHED THEN
    INSERT (Codigo, Nombre, Ruta, Icono, Modulo, Orden, CreadoPor)
    VALUES (source.Codigo, source.Nombre, source.Ruta, source.Icono, source.Modulo, source.Orden, 'SEED');
GO

-- D. Inserción de la Matriz de Permisos por Rol
-- 1. Rol Administrador: Acceso y todas las operaciones en todas las pantallas
INSERT INTO RolesPermisos (RolID, PantallaID, PermisoID, CreadoPor)
SELECT r.RolID, p.PantallaID, pm.PermisoID, 'SEED'
FROM Roles r
CROSS JOIN Pantallas p
CROSS JOIN Permisos pm
WHERE r.Nombre = 'Administrador'
  AND NOT EXISTS (
      SELECT 1 FROM RolesPermisos rp 
      WHERE rp.RolID = r.RolID AND rp.PantallaID = p.PantallaID AND rp.PermisoID = pm.PermisoID
  );
GO

-- 2. Rol Gerente: Operaciones en todo el sistema excepto configuración técnica (SEG_* y CFG_*)
INSERT INTO RolesPermisos (RolID, PantallaID, PermisoID, CreadoPor)
SELECT r.RolID, p.PantallaID, pm.PermisoID, 'SEED'
FROM Roles r
CROSS JOIN Pantallas p
CROSS JOIN Permisos pm
WHERE r.Nombre = 'Gerente'
  AND p.Codigo NOT IN ('SEG_USUARIOS', 'SEG_ROLES', 'CFG_CORRELATIVOS')
  AND NOT EXISTS (
      SELECT 1 FROM RolesPermisos rp 
      WHERE rp.RolID = r.RolID AND rp.PantallaID = p.PantallaID AND rp.PermisoID = pm.PermisoID
  );
GO

-- 3. Rol Vendedor: Ventas, Clientes, Facturación, Cajas e Inventario (lectura)
INSERT INTO RolesPermisos (RolID, PantallaID, PermisoID, CreadoPor)
SELECT r.RolID, p.PantallaID, pm.PermisoID, 'SEED'
FROM Roles r
JOIN Pantallas p ON p.Codigo IN ('CLI_CLIENTES', 'FAC_FACTURACION', 'FAC_CAJAS', 'INV_PRODUCTOS')
JOIN Permisos pm ON (
    (p.Codigo IN ('CLI_CLIENTES', 'FAC_FACTURACION', 'FAC_CAJAS') AND pm.Codigo IN ('ACCESO', 'CREAR', 'EDITAR', 'IMPRIMIR'))
    OR
    (p.Codigo = 'INV_PRODUCTOS' AND pm.Codigo IN ('ACCESO'))
)
WHERE r.Nombre = 'Vendedor'
  AND NOT EXISTS (
      SELECT 1 FROM RolesPermisos rp 
      WHERE rp.RolID = r.RolID AND rp.PantallaID = p.PantallaID AND rp.PermisoID = pm.PermisoID
  );
GO

-- 4. Rol Contador: Finanzas, Contabilidad, Facturas (consulta), Reportes BI
INSERT INTO RolesPermisos (RolID, PantallaID, PermisoID, CreadoPor)
SELECT r.RolID, p.PantallaID, pm.PermisoID, 'SEED'
FROM Roles r
JOIN Pantallas p ON p.Modulo IN ('Finanzas', 'BI') OR p.Codigo IN ('FAC_FACTURACION', 'FAC_NOTAS_CREDITO')
JOIN Permisos pm ON pm.Codigo IN ('ACCESO', 'CREAR', 'EDITAR', 'IMPRIMIR')
WHERE r.Nombre = 'Contador'
  AND NOT EXISTS (
      SELECT 1 FROM RolesPermisos rp 
      WHERE rp.RolID = r.RolID AND rp.PantallaID = p.PantallaID AND rp.PermisoID = pm.PermisoID
  );
GO

-- 5. Rol RH: Expedientes, Nómina y Vacaciones
INSERT INTO RolesPermisos (RolID, PantallaID, PermisoID, CreadoPor)
SELECT r.RolID, p.PantallaID, pm.PermisoID, 'SEED'
FROM Roles r
JOIN Pantallas p ON p.Modulo = 'RH'
JOIN Permisos pm ON pm.Codigo IN ('ACCESO', 'CREAR', 'EDITAR', 'IMPRIMIR')
WHERE r.Nombre = 'RH'
  AND NOT EXISTS (
      SELECT 1 FROM RolesPermisos rp 
      WHERE rp.RolID = r.RolID AND rp.PantallaID = p.PantallaID AND rp.PermisoID = pm.PermisoID
  );
GO

-- 6. Rol Consulta: Solo lectura (ACCESO) a módulos operativos
INSERT INTO RolesPermisos (RolID, PantallaID, PermisoID, CreadoPor)
SELECT r.RolID, p.PantallaID, pm.PermisoID, 'SEED'
FROM Roles r
CROSS JOIN Pantallas p
JOIN Permisos pm ON pm.Codigo = 'ACCESO'
WHERE r.Nombre = 'Consulta'
  AND p.Codigo NOT IN ('SEG_USUARIOS', 'SEG_ROLES', 'CFG_CORRELATIVOS')
  AND NOT EXISTS (
      SELECT 1 FROM RolesPermisos rp 
      WHERE rp.RolID = r.RolID AND rp.PantallaID = p.PantallaID AND rp.PermisoID = pm.PermisoID
  );
GO

-- E. Inserción del Usuario Administrador Inicial
-- Contraseña temporal: 'Admin2026!Temp' cifrada con BCrypt (fuerza 10)
DECLARE @AdminRolID INT;
SELECT @AdminRolID = RolID FROM Roles WHERE Nombre = 'Administrador';

IF NOT EXISTS (SELECT 1 FROM Usuarios WHERE Username = 'admin')
BEGIN
    INSERT INTO Usuarios (Username, PasswordHash, NombreCompleto, Email, Telefono, RolID, Activo, CreadoPor)
    VALUES (
        'admin',
        '$2a$10$/VE1djPX/6QG6./CX9FpyeY2tiUuUht8NQ9JgAlVgHDD9W31nOxSW',
        'Administrador del Sistema',
        'admin@erp.com.ni',
        '+505 8888-8888',
        @AdminRolID,
        1,
        'SEED'
    );
END
GO
