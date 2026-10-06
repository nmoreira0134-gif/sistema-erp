/**
 * SISTEMA ERP - NICARAGUA
 * ARCHIVO: LoginResponseDTO.java
 * PAQUETE: com.erp.sistema_erp.dto.seguridad
 *
 * QUÉ HACE:
 * Objeto de transferencia que transporta el JWT generado, datos del perfil autenticado,
 * el rol asignado y la lista de permisos/pantallas autorizadas.
 *
 * POR QUÉ EXISTE:
 * Entrega al frontend toda la información necesaria para almacenar el token en sessionStorage
 * y renderizar el panel administrativo según los privilegios del usuario.
 *
 * CÓMO SE CONECTA CON EL RESTO:
 * Es retornado por AuthService.autenticar() y consumido por auth.js en la vista web.
 */
package com.erp.sistema_erp.dto.seguridad;

import java.util.List;

public class LoginResponseDTO {

    private String token;
    private String tipoToken = "Bearer";
    private Integer usuarioId;
    private String username;
    private String nombreCompleto;
    private String email;
    private String rol;
    private List<String> permisos;
    private List<PantallaMenuDTO> menu;

    public LoginResponseDTO() {
    }

    public LoginResponseDTO(String token, Integer usuarioId, String username, String nombreCompleto,
                            String email, String rol, List<String> permisos, List<PantallaMenuDTO> menu) {
        this.token = token;
        this.tipoToken = "Bearer";
        this.usuarioId = usuarioId;
        this.username = username;
        this.nombreCompleto = nombreCompleto;
        this.email = email;
        this.rol = rol;
        this.permisos = permisos;
        this.menu = menu;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTipoToken() {
        return tipoToken;
    }

    public void setTipoToken(String tipoToken) {
        this.tipoToken = tipoToken;
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

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public List<String> getPermisos() {
        return permisos;
    }

    public void setPermisos(List<String> permisos) {
        this.permisos = permisos;
    }

    public List<PantallaMenuDTO> getMenu() {
        return menu;
    }

    public void setMenu(List<PantallaMenuDTO> menu) {
        this.menu = menu;
    }
}
