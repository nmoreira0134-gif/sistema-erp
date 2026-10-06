/**
 * SISTEMA ERP - NICARAGUA
 * ARCHIVO: RolController.java
 * PAQUETE: com.erp.sistema_erp.controller.seguridad
 *
 * QUÉ HACE:
 * Controlador REST para consultar los roles del ERP y gestionar la matriz de permisos por pantalla (/api/roles).
 *
 * POR QUÉ EXISTE:
 * Permite que los administradores visualicen los perfiles y ajusten qué funciones tiene asignadas cada rol.
 *
 * CÓMO SE CONECTA CON EL RESTO:
 * Invoca RolService y abastece la vista SEG_ROLES en el frontend.
 */
package com.erp.sistema_erp.controller.seguridad;

import com.erp.sistema_erp.dto.common.ApiResponse;
import com.erp.sistema_erp.dto.seguridad.MatrizPermisoDTO;
import com.erp.sistema_erp.dto.seguridad.RolResponseDTO;
import com.erp.sistema_erp.service.seguridad.RolService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/roles")
public class RolController {

    private final RolService rolService;

    public RolController(RolService rolService) {
        this.rolService = rolService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<RolResponseDTO>>> listar() {
        List<RolResponseDTO> roles = rolService.listarRoles();
        return ResponseEntity.ok(ApiResponse.exito(roles));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasAuthority('SEG_ROLES:ACCESO')")
    public ResponseEntity<ApiResponse<RolResponseDTO>> obtenerPorId(@PathVariable("id") Integer id) {
        RolResponseDTO rol = rolService.obtenerRolPorId(id);
        return ResponseEntity.ok(ApiResponse.exito(rol));
    }

    @GetMapping("/{id}/matriz")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasAuthority('SEG_ROLES:ACCESO')")
    public ResponseEntity<ApiResponse<List<MatrizPermisoDTO>>> obtenerMatrizPermisos(@PathVariable("id") Integer id) {
        List<MatrizPermisoDTO> matriz = rolService.obtenerMatrizPermisosPorRol(id);
        return ResponseEntity.ok(ApiResponse.exito(matriz));
    }

    @PostMapping("/{id}/permiso")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasAuthority('SEG_ROLES:EDITAR')")
    public ResponseEntity<ApiResponse<String>> cambiarPermiso(
            @PathVariable("id") Integer id,
            @RequestBody Map<String, Object> payload) {
        Integer pantallaId = (Integer) payload.get("pantallaId");
        Integer permisoId = (Integer) payload.get("permisoId");
        Boolean habilitar = (Boolean) payload.getOrDefault("habilitar", true);

        rolService.cambiarPermiso(id, pantallaId, permisoId, habilitar);
        return ResponseEntity.ok(ApiResponse.exito("Permiso actualizado exitosamente", null));
    }
}
