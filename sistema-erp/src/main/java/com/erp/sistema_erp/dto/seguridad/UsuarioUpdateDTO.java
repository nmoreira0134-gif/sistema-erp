/**
 * SISTEMA ERP - NICARAGUA
 * ARCHIVO: UsuarioUpdateDTO.java
 * PAQUETE: com.erp.sistema_erp.dto.seguridad
 *
 * QUÉ HACE:
 * Encapsula los campos modificables de un usuario existente (nombre, email, teléfono, rol y opcionalmente password).
 *
 * POR QUÉ EXISTE:
 * Evita la sobreescritura accidental del Username (que debe permanecer inmutable) y permite actualizar contraseñas de forma opcional.
 *
 * CÓMO SE CONECTA CON EL RESTO:
 * Es procesado por UsuarioService.actualizarUsuario() en el endpoint PUT /api/usuarios/{id}.
 */
package com.erp.sistema_erp.dto.seguridad;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UsuarioUpdateDTO {

    @NotBlank(message = "El nombre completo es obligatorio")
    @Size(max = 150, message = "El nombre no puede exceder 150 caracteres")
    private String nombreCompleto;

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El formato de correo no es válido")
    private String email;

    private String telefono;

    @NotNull(message = "El Rol es obligatorio")
    private Integer rolId;

    private String password;

    public UsuarioUpdateDTO() {
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Integer getRolId() {
        return rolId;
    }

    public void setRolId(Integer rolId) {
        this.rolId = rolId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
