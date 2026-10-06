/**
 * SISTEMA ERP - NICARAGUA
 * ARCHIVO: ClienteRepository.java
 * PAQUETE: com.erp.sistema_erp.repository.ventas
 *
 * QUÉ HACE:
 * Interfaz de persistencia Spring Data JPA para la entidad Cliente.
 *
 * POR QUÉ EXISTE:
 * Facilita operaciones CRUD paginadas, comprobaciones de unicidad de código y documento de identidad,
 * y búsquedas multicriterio para facturación.
 *
 * CÓMO SE CONECTA CON EL RESTO:
 * Es inyectada en ClienteService para todas las operaciones de persistencia en la base de datos SQL Server.
 */
package com.erp.sistema_erp.repository.ventas;

import com.erp.sistema_erp.model.ventas.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    Optional<Cliente> findByCodigoCliente(String codigoCliente);

    Optional<Cliente> findByDocumentoIdentidad(String documentoIdentidad);

    boolean existsByCodigoCliente(String codigoCliente);

    boolean existsByDocumentoIdentidad(String documentoIdentidad);

    @Query("SELECT c FROM Cliente c WHERE " +
           "LOWER(c.nombre) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
           "LOWER(c.apellido) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
           "(c.razonSocial IS NOT NULL AND LOWER(c.razonSocial) LIKE LOWER(CONCAT('%', :termino, '%'))) OR " +
           "LOWER(c.codigoCliente) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
           "LOWER(c.documentoIdentidad) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
           "(c.email IS NOT NULL AND LOWER(c.email) LIKE LOWER(CONCAT('%', :termino, '%')))")
    Page<Cliente> buscar(@Param("termino") String termino, Pageable pageable);

    @Query("SELECT c FROM Cliente c WHERE " +
           "LOWER(c.nombre) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
           "LOWER(c.apellido) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
           "(c.razonSocial IS NOT NULL AND LOWER(c.razonSocial) LIKE LOWER(CONCAT('%', :termino, '%'))) OR " +
           "LOWER(c.codigoCliente) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
           "LOWER(c.documentoIdentidad) LIKE LOWER(CONCAT('%', :termino, '%'))")
    List<Cliente> buscarRapido(@Param("termino") String termino);

    @Query("SELECT c.codigoCliente FROM Cliente c WHERE c.codigoCliente LIKE :prefijo ORDER BY c.codigoCliente DESC")
    List<String> findUltimosCodigosPorPrefijo(@Param("prefijo") String prefijo);

    @Query(value = "SELECT COUNT(1) FROM Clientes WHERE DocumentoIdentidad = :doc", nativeQuery = true)
    int contarPorDocumentoIdentidadTotal(@Param("doc") String doc);

    @Query(value = "SELECT COUNT(1) FROM Clientes WHERE CodigoCliente = :cod", nativeQuery = true)
    int contarPorCodigoClienteTotal(@Param("cod") String cod);
}
