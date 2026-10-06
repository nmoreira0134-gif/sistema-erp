/**
 * SISTEMA ERP - NICARAGUA
 * ARCHIVO: PantallaController.java
 * PAQUETE: com.erp.sistema_erp.controller.seguridad
 *
 * QUÉ HACE:
 * Controlador REST que entrega el menú dinámico autorizado para el usuario en sesión (/api/pantallas/menu)
 * y el catálogo general de pantallas del ERP (/api/pantallas).
 *
 * POR QUÉ EXISTE:
 * Es consultado al cargar el frontend para pintar los módulos y accesos del sidebar.
 *
 * CÓMO SE CONECTA CON EL RESTO:
 * Extrae los datos del CustomUserDetails autenticado e invoca PantallaService.
 */
package com.erp.sistema_erp.controller.seguridad;

import com.erp.sistema_erp.config.CustomUserDetails;
import com.erp.sistema_erp.dto.common.ApiResponse;
import com.erp.sistema_erp.dto.seguridad.PantallaMenuDTO;
import com.erp.sistema_erp.service.seguridad.PantallaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/pantallas")
public class PantallaController {

    private final PantallaService pantallaService;

    public PantallaController(PantallaService pantallaService) {
        this.pantallaService = pantallaService;
    }

    @GetMapping("/menu")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<PantallaMenuDTO>>> obtenerMenuUsuario(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Integer rolId = userDetails.getUsuario().getRol().getRolId();
        List<PantallaMenuDTO> menu = pantallaService.listarMenuPorRol(rolId);
        return ResponseEntity.ok(ApiResponse.exito(menu));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<List<PantallaMenuDTO>>> listarTodas() {
        List<PantallaMenuDTO> pantallas = pantallaService.listarTodas();
        return ResponseEntity.ok(ApiResponse.exito(pantallas));
    }
}
