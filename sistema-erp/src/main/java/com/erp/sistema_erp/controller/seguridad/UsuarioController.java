/**
 * SISTEMA ERP - NICARAGUA
 * ARCHIVO: UsuarioController.java
 * PAQUETE: com.erp.sistema_erp.controller.seguridad
 *
 * QUÉ HACE:
 * Controlador REST que expone operaciones de consulta, creación, actualización y cambio de estado
 * de usuarios del sistema ERP (/api/usuarios).
 *
 * POR QUÉ EXISTE:
 * Provee la interfaz programática para la administración de cuentas de usuario desde la pantalla SEG_USUARIOS.
 *
 * CÓMO SE CONECTA CON EL RESTO:
 * Es protegido mediante @PreAuthorize para verificar roles y permisos granulares (SEG_USUARIOS:CREAR, etc.)
 * e invoca a UsuarioService.
 */
package com.erp.sistema_erp.controller.seguridad;

import com.erp.sistema_erp.dto.common.ApiResponse;
import com.erp.sistema_erp.dto.seguridad.UsuarioCreateDTO;
import com.erp.sistema_erp.dto.seguridad.UsuarioResponseDTO;
import com.erp.sistema_erp.dto.seguridad.UsuarioUpdateDTO;
import com.erp.sistema_erp.service.seguridad.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasAuthority('SEG_USUARIOS:ACCESO')")
    public ResponseEntity<ApiResponse<List<UsuarioResponseDTO>>> listar() {
        List<UsuarioResponseDTO> usuarios = usuarioService.listarUsuarios();
        return ResponseEntity.ok(ApiResponse.exito("Usuarios recuperados con éxito", usuarios));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasAuthority('SEG_USUARIOS:ACCESO')")
    public ResponseEntity<ApiResponse<UsuarioResponseDTO>> obtenerPorId(@PathVariable("id") Integer id) {
        UsuarioResponseDTO usuario = usuarioService.obtenerPorId(id);
        return ResponseEntity.ok(ApiResponse.exito(usuario));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasAuthority('SEG_USUARIOS:CREAR')")
    public ResponseEntity<ApiResponse<UsuarioResponseDTO>> crear(@Valid @RequestBody UsuarioCreateDTO dto) {
        UsuarioResponseDTO creado = usuarioService.crearUsuario(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Usuario creado exitosamente", creado));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasAuthority('SEG_USUARIOS:EDITAR')")
    public ResponseEntity<ApiResponse<UsuarioResponseDTO>> actualizar(
            @PathVariable("id") Integer id,
            @Valid @RequestBody UsuarioUpdateDTO dto) {
        UsuarioResponseDTO actualizado = usuarioService.actualizarUsuario(id, dto);
        return ResponseEntity.ok(ApiResponse.exito("Usuario actualizado exitosamente", actualizado));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasAuthority('SEG_USUARIOS:ELIMINAR')")
    public ResponseEntity<ApiResponse<String>> cambiarEstado(
            @PathVariable("id") Integer id,
            @RequestBody Map<String, Boolean> payload) {
        Boolean activo = payload.getOrDefault("activo", false);
        usuarioService.cambiarEstado(id, activo);
        String mensaje = activo ? "Usuario activado correctamente" : "Usuario desactivado correctamente";
        return ResponseEntity.ok(ApiResponse.exito(mensaje, null));
    }
}
