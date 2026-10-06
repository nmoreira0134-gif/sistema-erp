/**
 * SISTEMA ERP - NICARAGUA
 * ARCHIVO: RolRepository.java
 * PAQUETE: com.erp.sistema_erp.repository.seguridad
 *
 * QUÉ HACE:
 * Interfaz de persistencia Spring Data JPA para la entidad Rol.
 *
 * POR QUÉ EXISTE:
 * Facilita operaciones CRUD y consultas derivadas sobre la tabla Roles.
 *
 * CÓMO SE CONECTA CON EL RESTO:
 * Es inyectada en RolService, UsuarioService y AuthService para validar y asignar perfiles.
 */
package com.erp.sistema_erp.repository.seguridad;

import com.erp.sistema_erp.model.seguridad.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {
    Optional<Rol> findByNombre(String nombre);
    boolean existsByNombre(String nombre);
}
