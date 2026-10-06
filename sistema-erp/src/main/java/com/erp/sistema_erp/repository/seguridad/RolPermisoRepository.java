/**
 * SISTEMA ERP - NICARAGUA
 * ARCHIVO: RolPermisoRepository.java
 * PAQUETE: com.erp.sistema_erp.repository.seguridad
 *
 * QUÉ HACE:
 * Interfaz de persistencia Spring Data JPA para la entidad asociativa RolPermiso.
 *
 * POR QUÉ EXISTE:
 * Consulta los permisos asignados a un rol sobre pantallas específicas para validar autorizaciones.
 *
 * CÓMO SE CONECTA CON EL RESTO:
 * Utilizada por CustomUserDetailsService para inyectar GrantedAuthorities y por PantallaService
 * para filtrar las opciones de menú que un usuario tiene permitido visualizar.
 */
package com.erp.sistema_erp.repository.seguridad;

import com.erp.sistema_erp.model.seguridad.RolPermiso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolPermisoRepository extends JpaRepository<RolPermiso, Integer> {

    List<RolPermiso> findByRol_RolId(Integer rolId);

    @Query("SELECT rp FROM RolPermiso rp WHERE rp.rol.rolId = :rolId AND rp.permiso.codigo = 'ACCESO' ORDER BY rp.pantalla.modulo ASC, rp.pantalla.orden ASC")
    List<RolPermiso> findPantallasAccesiblesPorRolId(@Param("rolId") Integer rolId);

    boolean existsByRol_RolIdAndPantalla_CodigoAndPermiso_Codigo(Integer rolId, String pantallaCodigo, String permisoCodigo);

    void deleteByRol_RolId(Integer rolId);
}
