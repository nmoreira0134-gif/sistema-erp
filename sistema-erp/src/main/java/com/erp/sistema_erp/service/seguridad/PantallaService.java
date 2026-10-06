/**
 * SISTEMA ERP - NICARAGUA
 * ARCHIVO: PantallaService.java
 * PAQUETE: com.erp.sistema_erp.service.seguridad
 *
 * QUÉ HACE:
 * Servicio de lógica de negocio para la consulta del catálogo de pantallas y construcción
 * del menú de navegación autorizado para cada rol.
 *
 * POR QUÉ EXISTE:
 * Filtra las opciones visuales del frontend según los permisos de acceso registrados en la BD.
 *
 * CÓMO SE CONECTA CON EL RESTO:
 * Utiliza PantallaRepository y RolPermisoRepository, y es consumido por PantallaController y AuthService.
 */
package com.erp.sistema_erp.service.seguridad;

import com.erp.sistema_erp.dto.seguridad.PantallaMenuDTO;
import com.erp.sistema_erp.model.seguridad.Pantalla;
import com.erp.sistema_erp.model.seguridad.RolPermiso;
import com.erp.sistema_erp.repository.seguridad.PantallaRepository;
import com.erp.sistema_erp.repository.seguridad.RolPermisoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PantallaService {

    private final PantallaRepository pantallaRepository;
    private final RolPermisoRepository rolPermisoRepository;

    public PantallaService(PantallaRepository pantallaRepository, RolPermisoRepository rolPermisoRepository) {
        this.pantallaRepository = pantallaRepository;
        this.rolPermisoRepository = rolPermisoRepository;
    }

    @Transactional(readOnly = true)
    public List<PantallaMenuDTO> listarMenuPorRol(Integer rolId) {
        List<RolPermiso> accesibles = rolPermisoRepository.findPantallasAccesiblesPorRolId(rolId);

        return accesibles.stream()
                .map(rp -> {
                    Pantalla p = rp.getPantalla();
                    return new PantallaMenuDTO(
                            p.getPantallaId(),
                            p.getCodigo(),
                            p.getNombre(),
                            p.getRuta(),
                            p.getIcono(),
                            p.getModulo(),
                            p.getOrden()
                    );
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PantallaMenuDTO> listarTodas() {
        return pantallaRepository.findAllByOrderByModuloAscOrdenAsc().stream()
                .map(p -> new PantallaMenuDTO(
                        p.getPantallaId(),
                        p.getCodigo(),
                        p.getNombre(),
                        p.getRuta(),
                        p.getIcono(),
                        p.getModulo(),
                        p.getOrden()
                ))
                .toList();
    }
}
