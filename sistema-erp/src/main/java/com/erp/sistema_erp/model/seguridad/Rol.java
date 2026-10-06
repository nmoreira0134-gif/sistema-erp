/**
 * SISTEMA ERP - NICARAGUA
 * ARCHIVO: Rol.java
 * PAQUETE: com.erp.sistema_erp.model.seguridad
 *
 * QUÉ HACE:
 * Representa la entidad JPA para la tabla Roles (Administrador, Gerente, Vendedor, etc.).
 *
 * POR QUÉ EXISTE:
 * Modela los niveles de acceso y perfiles de usuario exigidos en las convenciones del sistema ERP.
 *
 * CÓMO SE CONECTA CON EL RESTO:
 * Se asocia uno-a-muchos con la entidad Usuario y uno-a-muchos con RolPermiso para gobernar
 * las autorizaciones granulares en Spring Security.
 */
package com.erp.sistema_erp.model.seguridad;

import com.erp.sistema_erp.model.base.AuditableEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.SQLRestriction;

import java.util.Objects;

@Entity
@Table(name = "Roles")
@SQLRestriction("Activo = 1")
public class Rol extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RolID")
    private Integer rolId;

    @Column(name = "Nombre", length = 50, nullable = false, unique = true)
    private String nombre;

    @Column(name = "Descripcion", length = 255)
    private String descripcion;

    public Rol() {
    }

    public Rol(Integer rolId, String nombre, String descripcion) {
        this.rolId = rolId;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public Integer getRolId() {
        return rolId;
    }

    public void setRolId(Integer rolId) {
        this.rolId = rolId;
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
        if (!(o instanceof Rol rol)) return false;
        return Objects.equals(rolId, rol.rolId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rolId);
    }
}
