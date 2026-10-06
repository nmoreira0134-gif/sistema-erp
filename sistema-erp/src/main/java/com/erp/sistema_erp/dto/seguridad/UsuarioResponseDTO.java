/**
 * SISTEMA ERP - NICARAGUA
 * ARCHIVO: UsuarioResponseDTO.java
 * PAQUETE: com.erp.sistema_erp.dto.seguridad
 *
 * QUÉ HACE:
 * Proyecta la información pública y segura de un usuario para ser consumida en la vista de administración.
 *
 * POR QUÉ EXISTE:
 * Oculta el hash de contraseña (PasswordHash) y expone de forma amigable el nombre del rol.
 *
 * CÓMO SE CONECTA CON EL RESTO:
 * Es retornado en listas y detalles por UsuarioController hacia el frontend vanilla usuarios.js.
 */
package com.erp.sistema_erp.dto.seguridad;

import java.time.LocalDateTime;

public class UsuarioResponseDTO {

    private Integer usuarioId;
    private String username;
    private String nombreCompleto;
    private String email;
    private String telefono;
    private Integer rolId;
    private String rolNombre;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private String creadoPor;

    public UsuarioResponseDTO() {
    }

    public UsuarioResponseDTO(Integer usuarioId, String username, String nombreCompleto, String email,
                              String telefono, Integer rolId, String rolNombre, Boolean activo,
                              LocalDateTime fechaCreacion, String creadoPor) {
        this.usuarioId = usuarioId;
        this.username = username;
        this.nombreCompleto = nombreCompleto;
        this.email = email;
        this.telefono = telefono;
        this.rolId = rolId;
        this.rolNombre = rolNombre;
        this.activo = activo;
        this.fechaCreacion = fechaCreacion;
        this.creadoPor = creadoPor;
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getRolNombre() {
        return rolNombre;
    }

    public void setRolNombre(String rolNombre) {
        this.rolNombre = rolNombre;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getCreadoPor() {
        return creadoPor;
    }

    public void setCreadoPor(String creadoPor) {
        this.creadoPor = creadoPor;
    }
}
