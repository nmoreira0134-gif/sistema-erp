/**
 * SISTEMA ERP - NICARAGUA
 * ARCHIVO: CustomUserDetails.java
 * PAQUETE: com.erp.sistema_erp.config
 *
 * QUÉ HACE:
 * Implementación de UserDetails de Spring Security que adapta la entidad Usuario
 * y su colección de roles/permisos para el motor de seguridad.
 *
 * POR QUÉ EXISTE:
 * Permite que Spring Security evalúe tanto el rol principal (ej: ROLE_ADMINISTRADOR) como
 * permisos finos por pantalla (ej: FAC_FACTURACION:CREAR).
 *
 * CÓMO SE CONECTA CON EL RESTO:
 * Es instanciada por CustomUserDetailsService y almacenada en SecurityContextHolder tras validar el JWT.
 */
package com.erp.sistema_erp.config;

import com.erp.sistema_erp.model.seguridad.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final Usuario usuario;
    private final List<GrantedAuthority> authorities;

    public CustomUserDetails(Usuario usuario, List<GrantedAuthority> authorities) {
        this.usuario = usuario;
        this.authorities = authorities;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return usuario.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return usuario.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return usuario.getActivo() != null && usuario.getActivo();
    }
}
