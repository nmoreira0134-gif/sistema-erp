/**
 * SISTEMA ERP - NICARAGUA
 * ARCHIVO: PermisoRepository.java
 * PAQUETE: com.erp.sistema_erp.repository.seguridad
 *
 * QUÉ HACE:
 * Interfaz de persistencia Spring Data JPA para la entidad Permiso.
 *
 * POR QUÉ EXISTE:
 * Facilita la recuperación del catálogo base de operaciones (ACCESO, CREAR, EDITAR, etc.).
 *
 * CÓMO SE CONECTA CON EL RESTO:
 * Es inyectada en RolService para construir la matriz de autorizaciones por pantalla.
 */
package com.erp.sistema_erp.repository.seguridad;

import com.erp.sistema_erp.model.seguridad.Permiso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermisoRepository extends JpaRepository<Permiso, Integer> {
    Optional<Permiso> findByCodigo(String codigo);
}
