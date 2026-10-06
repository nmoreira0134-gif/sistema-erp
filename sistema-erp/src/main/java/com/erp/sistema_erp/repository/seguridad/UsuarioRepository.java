/**
 * SISTEMA ERP - NICARAGUA
 * ARCHIVO: UsuarioRepository.java
 * PAQUETE: com.erp.sistema_erp.repository.seguridad
 *
 * QUÉ HACE:
 * Interfaz de persistencia Spring Data JPA para la entidad Usuario.
 *
 * POR QUÉ EXISTE:
 * Proporciona métodos de búsqueda por nombre de usuario (Username) o Email, así como validaciones de existencia.
 *
 * CÓMO SE CONECTA CON EL RESTO:
 * Es utilizada por CustomUserDetailsService para autenticar credenciales y por UsuarioService para la gestión de cuentas.
 */
package com.erp.sistema_erp.repository.seguridad;

import com.erp.sistema_erp.model.seguridad.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByUsername(String username);
    Optional<Usuario> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
