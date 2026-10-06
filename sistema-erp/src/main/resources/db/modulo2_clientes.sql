-- ==============================================================================================
-- SISTEMA ERP - NICARAGUA
-- ARCHIVO: modulo2_clientes.sql
-- DESCRIPCIÓN: Script DDL para la creación de la tabla Clientes, índices de consulta rápida,
--                 actualización del catálogo de pantallas (VEN_CLIENTES) y asignación de permisos
--                 por rol según la matriz de seguridad aprobada.
-- POR QUÉ EXISTE: Define el modelo relacional del catálogo maestro de clientes del ERP, asegurando
--                 unicidad de códigos y documentos de identidad (Cédula/RUC) e integridad referencial.
-- CONEXIÓN CON EL RESTO: Se conectará directamente con Facturación (Factura.ClienteID),
--                        Notas de Crédito y Cuentas por Cobrar (CxC).
-- ==============================================================================================

USE SistemaERP;
GO

-- 1. Actualizar código y metadatos de la pantalla en Pantallas
UPDATE Pantallas 
SET Codigo = 'VEN_CLIENTES',
    Nombre = 'Gestion de Clientes',
    Ruta = '/ventas/clientes',
    Icono = 'fa-users',
    Modulo = 'Ventas',
    Orden = 1
WHERE Codigo = 'CLI_CLIENTES' OR Codigo = 'VEN_CLIENTES';
GO

-- 2. Asegurar que exista la pantalla si no estuviera
IF NOT EXISTS (SELECT 1 FROM Pantallas WHERE Codigo = 'VEN_CLIENTES')
BEGIN
    INSERT INTO Pantallas (Codigo, Nombre, Ruta, Icono, Modulo, Orden, CreadoPor)
    VALUES ('VEN_CLIENTES', 'Gestion de Clientes', '/ventas/clientes', 'fa-users', 'Ventas', 1, 'SEED');
END
GO

-- 3. Sincronizar permisos específicos de la pantalla VEN_CLIENTES para cada Rol
DECLARE @PantallaID INT;
SELECT @PantallaID = PantallaID FROM Pantallas WHERE Codigo = 'VEN_CLIENTES';

-- Limpiar asignaciones previas de esta pantalla
DELETE FROM RolesPermisos WHERE PantallaID = @PantallaID;

-- Administrador: ACCESO, CREAR, EDITAR, ELIMINAR, ANULAR, IMPRIMIR
INSERT INTO RolesPermisos (RolID, PantallaID, PermisoID, CreadoPor)
SELECT r.RolID, @PantallaID, p.PermisoID, 'MIG_M2'
FROM Roles r CROSS JOIN Permisos p
WHERE r.Nombre = 'Administrador';

-- Gerente: ACCESO, CREAR, EDITAR, IMPRIMIR
INSERT INTO RolesPermisos (RolID, PantallaID, PermisoID, CreadoPor)
SELECT r.RolID, @PantallaID, p.PermisoID, 'MIG_M2'
FROM Roles r CROSS JOIN Permisos p
WHERE r.Nombre = 'Gerente' AND p.Codigo IN ('ACCESO', 'CREAR', 'EDITAR', 'IMPRIMIR');

-- Vendedor: ACCESO, CREAR, EDITAR, IMPRIMIR
INSERT INTO RolesPermisos (RolID, PantallaID, PermisoID, CreadoPor)
SELECT r.RolID, @PantallaID, p.PermisoID, 'MIG_M2'
FROM Roles r CROSS JOIN Permisos p
WHERE r.Nombre = 'Vendedor' AND p.Codigo IN ('ACCESO', 'CREAR', 'EDITAR', 'IMPRIMIR');

-- Contador: ACCESO, IMPRIMIR
INSERT INTO RolesPermisos (RolID, PantallaID, PermisoID, CreadoPor)
SELECT r.RolID, @PantallaID, p.PermisoID, 'MIG_M2'
FROM Roles r CROSS JOIN Permisos p
WHERE r.Nombre = 'Contador' AND p.Codigo IN ('ACCESO', 'IMPRIMIR');

-- Consulta: ACCESO
INSERT INTO RolesPermisos (RolID, PantallaID, PermisoID, CreadoPor)
SELECT r.RolID, @PantallaID, p.PermisoID, 'MIG_M2'
FROM Roles r CROSS JOIN Permisos p
WHERE r.Nombre = 'Consulta' AND p.Codigo IN ('ACCESO');
GO

-- 4. TABLA: Clientes
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'Clientes')
BEGIN
    CREATE TABLE Clientes (
        ClienteID INT IDENTITY(1,1) NOT NULL,
        CodigoCliente VARCHAR(20) NOT NULL,
        Nombre VARCHAR(100) NOT NULL,
        Apellido VARCHAR(100) NOT NULL,
        RazonSocial VARCHAR(200) NULL,
        TipoCliente VARCHAR(20) NOT NULL, -- 'Natural' o 'Juridico'
        DocumentoIdentidad VARCHAR(30) NOT NULL,
        Email VARCHAR(150) NULL,
        Telefono VARCHAR(30) NULL,
        Direccion VARCHAR(255) NULL,
        Ciudad VARCHAR(100) NULL,
        Pais VARCHAR(100) NOT NULL DEFAULT 'Nicaragua',
        LimiteCredito DECIMAL(12,2) NOT NULL DEFAULT 0.00,
        DiasCredito INT NOT NULL DEFAULT 0,
        Activo BIT NOT NULL DEFAULT 1,
        FechaEliminacion DATETIME NULL,
        CreadoPor VARCHAR(50) NOT NULL DEFAULT 'SYSTEM',
        FechaCreacion DATETIME NOT NULL DEFAULT GETDATE(),
        ModificadoPor VARCHAR(50) NULL,
        FechaModificacion DATETIME NULL,
        CONSTRAINT PK_Clientes PRIMARY KEY CLUSTERED (ClienteID),
        CONSTRAINT UQ_Clientes_CodigoCliente UNIQUE (CodigoCliente),
        CONSTRAINT UQ_Clientes_DocumentoIdentidad UNIQUE (DocumentoIdentidad),
        CONSTRAINT CK_Clientes_TipoCliente CHECK (TipoCliente IN ('Natural', 'Juridico')),
        CONSTRAINT CK_Clientes_LimiteCredito CHECK (LimiteCredito >= 0),
        CONSTRAINT CK_Clientes_DiasCredito CHECK (DiasCredito >= 0)
    );
END
GO

-- 5. Índices para Búsqueda Eficiente
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'IX_Clientes_Nombre_Apellido')
    CREATE NONCLUSTERED INDEX IX_Clientes_Nombre_Apellido ON Clientes(Nombre, Apellido);
GO

IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'IX_Clientes_RazonSocial')
    CREATE NONCLUSTERED INDEX IX_Clientes_RazonSocial ON Clientes(RazonSocial);
GO

IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'IX_Clientes_Email')
    CREATE NONCLUSTERED INDEX IX_Clientes_Email ON Clientes(Email);
GO
