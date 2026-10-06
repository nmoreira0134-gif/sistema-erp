/**
 * SISTEMA ERP - NICARAGUA
 * ARCHIVO: AuthController.java
 * PAQUETE: com.erp.sistema_erp.controller.seguridad
 *
 * QUÉ HACE:
 * Controlador REST que expone los endpoints públicos de autenticación e inicio de sesión (/api/auth/login)
 * y de consulta del perfil de sesión (/api/auth/perfil).
 *
 * POR QUÉ EXISTE:
 * Es la puerta de enlace para que el frontend obtenga el JWT necesario para comunicarse con el ERP.
 *
 * CÓMO SE CONECTA CON EL RESTO:
 * Recibe LoginRequestDTO, invoca AuthService y retorna un ApiResponse<LoginResponseDTO>.
 */
package com.erp.sistema_erp.controller.seguridad;

import com.erp.sistema_erp.dto.common.ApiResponse;
import com.erp.sistema_erp.dto.seguridad.LoginRequestDTO;
import com.erp.sistema_erp.dto.seguridad.LoginResponseDTO;
import com.erp.sistema_erp.service.seguridad.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> login(@Valid @RequestBody LoginRequestDTO request) {
        LoginResponseDTO respuesta = authService.autenticar(request);
        return ResponseEntity.ok(ApiResponse.exito("Autenticación exitosa", respuesta));
    }

    @GetMapping("/perfil")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> obtenerPerfil(Authentication authentication) {
        String username = authentication.getName();
        LoginResponseDTO perfil = authService.obtenerPerfil(username);
        return ResponseEntity.ok(ApiResponse.exito("Perfil recuperado correctamente", perfil));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout() {
        return ResponseEntity.ok(ApiResponse.exito("Sesión finalizada exitosamente", null));
    }
}
