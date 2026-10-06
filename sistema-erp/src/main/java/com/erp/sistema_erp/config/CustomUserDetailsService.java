/**
 * SISTEMA ERP - NICARAGUA
 * ARCHIVO: CustomUserDetailsService.java
 * PAQUETE: com.erp.sistema_erp.config
 *
 * QUÉ HACE:
 * Servicio que implementa UserDetailsService de Spring Security para cargar el usuario desde la BD
 * y armar sus autoridades (Role + Permisos por pantalla).
 *
 * POR QUÉ EXISTE:
 * Centraliza la extracción de credenciales y permisos durante el proceso de login y la validación de tokens JWT.
 *
 * CÓMO SE CONECTA CON EL RESTO:
 * Es inyectada en el AuthenticationManager y en JwtAuthenticationFilter.
 */
package com.erp.sistema_erp.config;

import com.erp.sistema_erp.model.seguridad.RolPermiso;
import com.erp.sistema_erp.model.seguridad.Usuario;
import com.erp.sistema_erp.repository.seguridad.RolPermisoRepository;
import com.erp.sistema_erp.repository.seguridad.UsuarioRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final RolPermisoRepository rolPermisoRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository, RolPermisoRepository rolPermisoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.rolPermisoRepository = rolPermisoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con username: " + username));

        List<GrantedAuthority> authorities = new ArrayList<>();

        // Rol principal con prefijo ROLE_
        if (usuario.getRol() != null) {
            String roleName = "ROLE_" + usuario.getRol().getNombre().toUpperCase().replace(" ", "_");
            authorities.add(new SimpleGrantedAuthority(roleName));

            // Permisos granulares: PANTALLA:PERMISO (ej: FAC_FACTURACION:CREAR)
            List<RolPermiso> permisos = rolPermisoRepository.findByRol_RolId(usuario.getRol().getRolId());
            for (RolPermiso rp : permisos) {
                if (rp.getPantalla() != null && rp.getPermiso() != null) {
                    String authCode = rp.getPantalla().getCodigo() + ":" + rp.getPermiso().getCodigo();
                    authorities.add(new SimpleGrantedAuthority(authCode));
                }
            }
        }

        return new CustomUserDetails(usuario, authorities);
    }
}
