/**
 * SISTEMA ERP - NICARAGUA
 * ARCHIVO: AuthService.java
 * PAQUETE: com.erp.sistema_erp.service.seguridad
 *
 * QUÉ HACE:
 * Servicio de lógica de negocio para la autenticación de usuarios y obtención del perfil de sesión.
 *
 * POR QUÉ EXISTE:
 * Centraliza la validación de credenciales a través del AuthenticationManager de Spring Security,
 * la generación del token JWT y la construcción del paquete de bienvenida (roles, permisos, pantallas).
 *
 * CÓMO SE CONECTA CON EL RESTO:
 * Es invocado por AuthController y utiliza JwtUtil, UsuarioRepository, CustomUserDetailsService y PantallaService.
 */
package com.erp.sistema_erp.service.seguridad;

import com.erp.sistema_erp.config.JwtUtil;
import com.erp.sistema_erp.dto.seguridad.LoginRequestDTO;
import com.erp.sistema_erp.dto.seguridad.LoginResponseDTO;
import com.erp.sistema_erp.dto.seguridad.PantallaMenuDTO;
import com.erp.sistema_erp.exception.BusinessException;
import com.erp.sistema_erp.model.seguridad.Usuario;
import com.erp.sistema_erp.repository.seguridad.UsuarioRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final UsuarioRepository usuarioRepository;
    private final PantallaService pantallaService;

    public AuthService(AuthenticationManager authenticationManager,
                       JwtUtil jwtUtil,
                       UserDetailsService userDetailsService,
                       UsuarioRepository usuarioRepository,
                       PantallaService pantallaService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.usuarioRepository = usuarioRepository;
        this.pantallaService = pantallaService;
    }

    @Transactional(readOnly = true)
    public LoginResponseDTO autenticar(LoginRequestDTO request) {
        // Autentica credenciales con Spring Security (lanza BadCredentialsException si falla)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuario = usuarioRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new BusinessException("Usuario no encontrado en la base de datos"));

        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new BusinessException("El usuario se encuentra inactivo. Contacte al administrador.");
        }

        // Construir claims adicionales
        Map<String, Object> claims = new HashMap<>();
        String rolNombre = usuario.getRol() != null ? usuario.getRol().getNombre() : "SIN_ROL";
        claims.put("rol", rolNombre);
        claims.put("nombreCompleto", usuario.getNombreCompleto());
        claims.put("usuarioId", usuario.getUsuarioId());

        String token = jwtUtil.generateToken(userDetails, claims);

        List<String> permisos = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        List<PantallaMenuDTO> menu = pantallaService.listarMenuPorRol(usuario.getRol().getRolId());

        return new LoginResponseDTO(
                token,
                usuario.getUsuarioId(),
                usuario.getUsername(),
                usuario.getNombreCompleto(),
                usuario.getEmail(),
                rolNombre,
                permisos,
                menu
        );
    }

    @Transactional(readOnly = true)
    public LoginResponseDTO obtenerPerfil(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("Usuario no encontrado: " + username));

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        List<String> permisos = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        List<PantallaMenuDTO> menu = pantallaService.listarMenuPorRol(usuario.getRol().getRolId());

        return new LoginResponseDTO(
                null,
                usuario.getUsuarioId(),
                usuario.getUsername(),
                usuario.getNombreCompleto(),
                usuario.getEmail(),
                usuario.getRol().getNombre(),
                permisos,
                menu
        );
    }
}
