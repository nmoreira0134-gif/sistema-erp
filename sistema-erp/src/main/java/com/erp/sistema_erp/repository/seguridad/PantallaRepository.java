/**
 * SISTEMA ERP - NICARAGUA
 * ARCHIVO: PantallaRepository.java
 * PAQUETE: com.erp.sistema_erp.repository.seguridad
 *
 * QUÉ HACE:
 * Interfaz de persistencia Spring Data JPA para la entidad Pantalla.
 *
 * POR QUÉ EXISTE:
 * Permite consultar el catálogo de pantallas ordenadas por módulo y secuencia para el frontend.
 *
 * CÓMO SE CONECTA CON EL RESTO:
 * Es utilizada por PantallaService para generar la estructura del menú y por RolService para asignar permisos.
 */
package com.erp.sistema_erp.repository.seguridad;

import com.erp.sistema_erp.model.seguridad.Pantalla;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PantallaRepository extends JpaRepository<Pantalla, Integer> {
    Optional<Pantalla> findByCodigo(String codigo);
    List<Pantalla> findAllByOrderByModuloAscOrdenAsc();
    List<Pantalla> findByModuloOrderByOrdenAsc(String modulo);
}
