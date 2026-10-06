/**
 * SISTEMA ERP - NICARAGUA
 * ARCHIVO: Pantalla.java
 * PAQUETE: com.erp.sistema_erp.model.seguridad
 *
 * QUÉ HACE:
 * Modela las opciones de menú y pantallas operativas del ERP organizadas por módulo.
 *
 * POR QUÉ EXISTE:
 * Permite renderizar dinámicamente el sidebar en el frontend y validar a qué vistas
 * y endpoints tiene acceso un usuario según su rol.
 *
 * CÓMO SE CONECTA CON EL RESTO:
 * Es referenciada por RolPermiso para autorizar el acceso y por el endpoint /api/pantallas/menu
 * para estructurar el panel de navegación web.
 */
package com.erp.sistema_erp.model.seguridad;

import com.erp.sistema_erp.model.base.AuditableEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.SQLRestriction;

import java.util.Objects;

@Entity
@Table(name = "Pantallas")
@SQLRestriction("Activo = 1")
public class Pantalla extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PantallaID")
    private Integer pantallaId;

    @Column(name = "Codigo", length = 50, nullable = false, unique = true)
    private String codigo;

    @Column(name = "Nombre", length = 100, nullable = false)
    private String nombre;

    @Column(name = "Ruta", length = 100, nullable = false)
    private String ruta;

    @Column(name = "Icono", length = 50)
    private String icono;

    @Column(name = "Modulo", length = 50, nullable = false)
    private String modulo;

    @Column(name = "Orden", nullable = false)
    private Integer orden = 1;

    public Pantalla() {
    }

    public Integer getPantallaId() {
        return pantallaId;
    }

    public void setPantallaId(Integer pantallaId) {
        this.pantallaId = pantallaId;
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

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public String getIcono() {
        return icono;
    }

    public void setIcono(String icono) {
        this.icono = icono;
    }

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pantalla pantalla)) return false;
        return Objects.equals(pantallaId, pantalla.pantallaId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pantallaId);
    }
}
