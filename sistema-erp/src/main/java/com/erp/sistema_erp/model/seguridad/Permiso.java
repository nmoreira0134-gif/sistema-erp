/**
 * SISTEMA ERP - NICARAGUA
 * ARCHIVO: Permiso.java
 * PAQUETE: com.erp.sistema_erp.model.seguridad
 *
 * QUÉ HACE:
 * Modela las operaciones atómicas del sistema (ACCESO, CREAR, EDITAR, ELIMINAR, ANULAR, IMPRIMIR).
 *
 * POR QUÉ EXISTE:
 * Facilita una autorización granular en los endpoints de la API y botones de la interfaz web.
 *
 * CÓMO SE CONECTA CON EL RESTO:
 * Es relacionado en RolPermiso para asociar una acción permitida a un rol sobre una pantalla concreta.
 */
package com.erp.sistema_erp.model.seguridad;

import com.erp.sistema_erp.model.base.AuditableEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.SQLRestriction;

import java.util.Objects;

@Entity
@Table(name = "Permisos")
@SQLRestriction("Activo = 1")
public class Permiso extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PermisoID")
    private Integer permisoId;

    @Column(name = "Codigo", length = 50, nullable = false, unique = true)
    private String codigo;

    @Column(name = "Nombre", length = 100, nullable = false)
    private String nombre;

    @Column(name = "Descripcion", length = 255)
    private String descripcion;

    public Permiso() {
    }

    public Integer getPermisoId() {
        return permisoId;
    }

    public void setPermisoId(Integer permisoId) {
        this.permisoId = permisoId;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Permiso permiso)) return false;
        return Objects.equals(permisoId, permiso.permisoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(permisoId);
    }
}
