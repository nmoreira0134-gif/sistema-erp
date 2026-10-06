/**
 * SISTEMA ERP - NICARAGUA
 * ARCHIVO: RolService.java
 * PAQUETE: com.erp.sistema_erp.service.seguridad
 *
 * QUÉ HACE:
 * Servicio de lógica de negocio para la administración de roles y su matriz de permisos.
 *
 * POR QUÉ EXISTE:
 * Centraliza la consulta de perfiles y la asignación/revocación de privilegios sobre pantallas del ERP.
 *
 * CÓMO SE CONECTA CON EL RESTO:
 * Utiliza RolRepository, PantallaRepository, PermisoRepository y RolPermisoRepository, y abastece a RolController.
 */
package com.erp.sistema_erp.service.seguridad;

import com.erp.sistema_erp.dto.seguridad.MatrizPermisoDTO;
import com.erp.sistema_erp.dto.seguridad.RolResponseDTO;
import com.erp.sistema_erp.exception.BusinessException;
import com.erp.sistema_erp.exception.ResourceNotFoundException;
import com.erp.sistema_erp.model.seguridad.Pantalla;
import com.erp.sistema_erp.model.seguridad.Permiso;
import com.erp.sistema_erp.model.seguridad.Rol;
import com.erp.sistema_erp.model.seguridad.RolPermiso;
import com.erp.sistema_erp.repository.seguridad.PantallaRepository;
import com.erp.sistema_erp.repository.seguridad.PermisoRepository;
import com.erp.sistema_erp.repository.seguridad.RolPermisoRepository;
import com.erp.sistema_erp.repository.seguridad.RolRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RolService {

    private final RolRepository rolRepository;
    private final PantallaRepository pantallaRepository;
    private final PermisoRepository permisoRepository;
    private final RolPermisoRepository rolPermisoRepository;

    public RolService(RolRepository rolRepository,
                      PantallaRepository pantallaRepository,
                      PermisoRepository permisoRepository,
                      RolPermisoRepository rolPermisoRepository) {
        this.rolRepository = rolRepository;
        this.pantallaRepository = pantallaRepository;
        this.permisoRepository = permisoRepository;
        this.rolPermisoRepository = rolPermisoRepository;
    }

    @Transactional(readOnly = true)
    public List<RolResponseDTO> listarRoles() {
        return rolRepository.findAll().stream()
                .map(this::convertirAResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public RolResponseDTO obtenerRolPorId(Integer rolId) {
        Rol rol = rolRepository.findById(rolId)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + rolId));
        return convertirAResponseDTO(rol);
    }

    @Transactional(readOnly = true)
    public List<MatrizPermisoDTO> obtenerMatrizPermisosPorRol(Integer rolId) {
        Rol rol = rolRepository.findById(rolId)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + rolId));

        List<Pantalla> pantallas = pantallaRepository.findAllByOrderByModuloAscOrdenAsc();
        List<RolPermiso> asignados = rolPermisoRepository.findByRol_RolId(rol.getRolId());

        Map<Integer, List<String>> permisosPorPantalla = asignados.stream()
                .collect(Collectors.groupingBy(
                        rp -> rp.getPantalla().getPantallaId(),
                        Collectors.mapping(rp -> rp.getPermiso().getCodigo(), Collectors.toList())
                ));

        return pantallas.stream()
                .map(p -> new MatrizPermisoDTO(
                        p.getPantallaId(),
                        p.getCodigo(),
                        p.getNombre(),
                        p.getModulo(),
                        permisosPorPantalla.getOrDefault(p.getPantallaId(), new ArrayList<>())
                ))
                .toList();
    }

    @Transactional
    public void cambiarPermiso(Integer rolId, Integer pantallaId, Integer permisoId, boolean habilitar) {
        Rol rol = rolRepository.findById(rolId)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + rolId));
        Pantalla pantalla = pantallaRepository.findById(pantallaId)
                .orElseThrow(() -> new ResourceNotFoundException("Pantalla no encontrada con ID: " + pantallaId));
        Permiso permiso = permisoRepository.findById(permisoId)
                .orElseThrow(() -> new ResourceNotFoundException("Permiso no encontrado con ID: " + permisoId));

        List<RolPermiso> existentes = rolPermisoRepository.findByRol_RolId(rolId);
        RolPermiso actual = existentes.stream()
                .filter(rp -> rp.getPantalla().getPantallaId().equals(pantallaId) && rp.getPermiso().getPermisoId().equals(permisoId))
                .findFirst()
                .orElse(null);

        if (habilitar) {
            if (actual == null) {
                RolPermiso nuevo = new RolPermiso(rol, pantalla, permiso);
                nuevo.setActivo(true);
                rolPermisoRepository.save(nuevo);
            } else if (!Boolean.TRUE.equals(actual.getActivo())) {
                actual.setActivo(true);
                actual.setFechaEliminacion(null);
                rolPermisoRepository.save(actual);
            }
        } else {
            if (actual != null) {
                actual.setActivo(false);
                actual.setFechaEliminacion(LocalDateTime.now());
                rolPermisoRepository.save(actual);
            }
        }
    }

    private RolResponseDTO convertirAResponseDTO(Rol rol) {
        List<RolPermiso> permisos = rolPermisoRepository.findByRol_RolId(rol.getRolId());
        List<String> codigosPermisos = permisos.stream()
                .map(rp -> rp.getPantalla().getCodigo() + ":" + rp.getPermiso().getCodigo())
                .toList();

        return new RolResponseDTO(
                rol.getRolId(),
                rol.getNombre(),
                rol.getDescripcion(),
                rol.getActivo(),
                codigosPermisos
        );
    }
}
