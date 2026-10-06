/**
 * SISTEMA ERP - NICARAGUA
 * ARCHIVO: RolPermiso.java
 * PAQUETE: com.erp.sistema_erp.model.seguridad
 *
 * QUÉ HACE:
 * Entidad de asociación ternaria entre Rol, Pantalla y Permiso (tabla RolesPermisos).
 *
 * POR QUÉ EXISTE:
 * Modela la matriz de control de acceso basada en roles (RBAC) con granularidad por pantalla.
 *
 * CÓMO SE CONECTA CON EL RESTO:
 * Es consultada por CustomUserDetailsService para construir las Authorities (GrantedAuthority)
 * del usuario autenticado en Spring Security y por el menú dinámico del frontend.
 */
package com.erp.sistema_erp.model.seguridad;

import com.erp.sistema_erp.model.base.AuditableEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.SQLRestriction;

import java.util.Objects;

@Entity
@Table(name = "RolesPermisos", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"RolID", "PantallaID", "PermisoID"})
})
@SQLRestriction("Activo = 1")
public class RolPermiso extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RolPermisoID")
    private Integer rolPermisoId;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "RolID", nullable = false)
    private Rol rol;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "PantallaID", nullable = false)
    private Pantalla pantalla;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "PermisoID", nullable = false)
    private Permiso permiso;

    public RolPermiso() {
    }

    public RolPermiso(Rol rol, Pantalla pantalla, Permiso permiso) {
        this.rol = rol;
        this.pantalla = pantalla;
        this.permiso = permiso;
    }

    public Integer getRolPermisoId() {
        return rolPermisoId;
    }

    public void setRolPermisoId(Integer rolPermisoId) {
        this.rolPermisoId = rolPermisoId;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public Pantalla getPantalla() {
        return pantalla;
    }

    public void setPantalla(Pantalla pantalla) {
        this.pantalla = pantalla;
    }

    public Permiso getPermiso() {
        return permiso;
    }

    public void setPermiso(Permiso permiso) {
        this.permiso = permiso;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RolPermiso that)) return false;
        return Objects.equals(rolPermisoId, that.rolPermisoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rolPermisoId);
    }
}
