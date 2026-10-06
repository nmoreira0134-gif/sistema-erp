/**
 * SISTEMA ERP - NICARAGUA
 * ARCHIVO: AuditableEntity.java
 * PAQUETE: com.erp.sistema_erp.model.base
 *
 * QUÉ HACE:
 * Define la superclase abstracta con los atributos comunes de auditoría (CreadoPor, FechaCreacion,
 * ModificadoPor, FechaModificacion) y borrado lógico (Activo, FechaEliminacion).
 *
 * POR QUÉ EXISTE:
 * Estandariza la trazabilidad obligatoria y el soft delete en todas las entidades del ERP,
 * evitando duplicación de código y garantizando cumplimiento normativo.
 *
 * CÓMO SE CONECTA CON EL RESTO:
 * Es heredada por todas las entidades del modelo de dominio (Roles, Usuarios, Facturas, Kardex, etc.).
 * Se integra con Spring Data JPA Auditing (AuditorAwareImpl) para poblar automáticamente los campos de autoría.
 */
package com.erp.sistema_erp.model.base;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditableEntity {

    @Column(name = "Activo", nullable = false)
    private Boolean activo = true;

    @Column(name = "FechaEliminacion")
    private LocalDateTime fechaEliminacion;

    @CreatedBy
    @Column(name = "CreadoPor", length = 50, updatable = false)
    private String creadoPor;

    @CreatedDate
    @Column(name = "FechaCreacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @LastModifiedBy
    @Column(name = "ModificadoPor", length = 50)
    private String modificadoPor;

    @LastModifiedDate
    @Column(name = "FechaModificacion")
    private LocalDateTime fechaModificacion;

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getFechaEliminacion() {
        return fechaEliminacion;
    }

    public void setFechaEliminacion(LocalDateTime fechaEliminacion) {
        this.fechaEliminacion = fechaEliminacion;
    }

    public String getCreadoPor() {
        return creadoPor;
    }

    public void setCreadoPor(String creadoPor) {
        this.creadoPor = creadoPor;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getModificadoPor() {
        return modificadoPor;
    }

    public void setModificadoPor(String modificadoPor) {
        this.modificadoPor = modificadoPor;
    }

    public LocalDateTime getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(LocalDateTime fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }
}
