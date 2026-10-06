/**
 * SISTEMA ERP - NICARAGUA
 * ARCHIVO: UsuarioService.java
 * PAQUETE: com.erp.sistema_erp.service.seguridad
 *
 * QUÉ HACE:
 * Servicio de lógica de negocio para la administración integral de usuarios (CRUD, soft delete, cifrado BCrypt).
 *
 * POR QUÉ EXISTE:
 * Aplica validaciones de unicidad de username y email, gestiona la asignación de roles y realiza
 * el borrado lógico (Activo = false, FechaEliminacion = now()).
 *
 * CÓMO SE CONECTA CON EL RESTO:
 * Utiliza UsuarioRepository, RolRepository y PasswordEncoder, y expone operaciones a UsuarioController.
 */
package com.erp.sistema_erp.service.seguridad;

import com.erp.sistema_erp.dto.seguridad.UsuarioCreateDTO;
import com.erp.sistema_erp.dto.seguridad.UsuarioResponseDTO;
import com.erp.sistema_erp.dto.seguridad.UsuarioUpdateDTO;
import com.erp.sistema_erp.exception.BusinessException;
import com.erp.sistema_erp.exception.ResourceNotFoundException;
import com.erp.sistema_erp.model.seguridad.Rol;
import com.erp.sistema_erp.model.seguridad.Usuario;
import com.erp.sistema_erp.repository.seguridad.RolRepository;
import com.erp.sistema_erp.repository.seguridad.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, RolRepository rolRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(this::convertirAResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO obtenerPorId(Integer usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));
        return convertirAResponseDTO(usuario);
    }

    @Transactional
    public UsuarioResponseDTO crearUsuario(UsuarioCreateDTO dto) {
        if (usuarioRepository.existsByUsername(dto.getUsername())) {
            throw new BusinessException("El nombre de usuario '" + dto.getUsername() + "' ya está registrado");
        }

        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("El correo electrónico '" + dto.getEmail() + "' ya está en uso");
        }

        Rol rol = rolRepository.findById(dto.getRolId())
                .orElseThrow(() -> new ResourceNotFoundException("El Rol seleccionado no existe"));

        Usuario nuevo = new Usuario();
        nuevo.setUsername(dto.getUsername().trim().toLowerCase());
        nuevo.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        nuevo.setNombreCompleto(dto.getNombreCompleto().trim());
        nuevo.setEmail(dto.getEmail().trim().toLowerCase());
        nuevo.setTelefono(dto.getTelefono() != null ? dto.getTelefono().trim() : null);
        nuevo.setRol(rol);
        nuevo.setActivo(true);

        Usuario guardado = usuarioRepository.save(nuevo);
        return convertirAResponseDTO(guardado);
    }

    @Transactional
    public UsuarioResponseDTO actualizarUsuario(Integer usuarioId, UsuarioUpdateDTO dto) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));

        // Validar unicidad de email si cambió
        if (!usuario.getEmail().equalsIgnoreCase(dto.getEmail().trim()) && usuarioRepository.existsByEmail(dto.getEmail().trim())) {
            throw new BusinessException("El correo electrónico '" + dto.getEmail() + "' ya está registrado por otro usuario");
        }

        Rol rol = rolRepository.findById(dto.getRolId())
                .orElseThrow(() -> new ResourceNotFoundException("El Rol seleccionado no existe"));

        usuario.setNombreCompleto(dto.getNombreCompleto().trim());
        usuario.setEmail(dto.getEmail().trim().toLowerCase());
        usuario.setTelefono(dto.getTelefono() != null ? dto.getTelefono().trim() : null);
        usuario.setRol(rol);

        // Si se suministró nueva contraseña, actualizarla cifrada
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            if (dto.getPassword().length() < 6) {
                throw new BusinessException("La nueva contraseña debe tener al menos 6 caracteres");
            }
            usuario.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        }

        Usuario actualizado = usuarioRepository.save(usuario);
        return convertirAResponseDTO(actualizado);
    }

    @Transactional
    public void cambiarEstado(Integer usuarioId, boolean activo) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));

        // Prohibir desactivar al usuario admin maestro
        if ("admin".equalsIgnoreCase(usuario.getUsername()) && !activo) {
            throw new BusinessException("No es posible desactivar al usuario administrador maestro del sistema");
        }

        usuario.setActivo(activo);
        if (!activo) {
            usuario.setFechaEliminacion(LocalDateTime.now());
        } else {
            usuario.setFechaEliminacion(null);
        }
        usuarioRepository.save(usuario);
    }

    private UsuarioResponseDTO convertirAResponseDTO(Usuario u) {
        return new UsuarioResponseDTO(
                u.getUsuarioId(),
                u.getUsername(),
                u.getNombreCompleto(),
                u.getEmail(),
                u.getTelefono(),
                u.getRol() != null ? u.getRol().getRolId() : null,
                u.getRol() != null ? u.getRol().getNombre() : "SIN_ROL",
                u.getActivo(),
                u.getFechaCreacion(),
                u.getCreadoPor()
        );
    }
}
