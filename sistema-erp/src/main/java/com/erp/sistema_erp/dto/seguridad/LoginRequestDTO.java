/**
 * SISTEMA ERP - NICARAGUA
 * ARCHIVO: LoginRequestDTO.java
 * PAQUETE: com.erp.sistema_erp.dto.seguridad
 *
 * QUÉ HACE:
 * Encapsula las credenciales de inicio de sesión (usuario y contraseña) enviadas desde el frontend.
 *
 * POR QUÉ EXISTE:
 * Valida los datos requeridos antes de procesar la autenticación en el endpoint /api/auth/login.
 *
 * CÓMO SE CONECTA CON EL RESTO:
 * Es recibido como @RequestBody en AuthController y validado con Jakarta Validation (@NotBlank).
 */
package com.erp.sistema_erp.dto.seguridad;

import jakarta.validation.constraints.NotBlank;

public class LoginRequestDTO {

    @NotBlank(message = "El nombre de usuario es obligatorio")
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    public LoginRequestDTO() {
    }

    public LoginRequestDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
