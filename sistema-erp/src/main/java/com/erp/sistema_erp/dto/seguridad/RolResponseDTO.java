/**
 * SISTEMA ERP - NICARAGUA
 * ARCHIVO: RolResponseDTO.java
 * PAQUETE: com.erp.sistema_erp.dto.seguridad
 *
 * QUÉ HACE:
 * DTO que describe un rol del sistema y su lista de permisos asignados.
 *
 * POR QUÉ EXISTE:
 * Facilita el llenado de combos (selects) de asignación de rol y la consulta de perfiles.
 *
 * CÓMO SE CONECTA CON EL RESTO:
 * Es devuelto por RolController y consumido por los formularios de creación de usuarios y matriz de roles.
 */
package com.erp.sistema_erp.dto.seguridad;

import java.util.List;

public class RolResponseDTO {

    private Integer rolId;
    private String nombre;
    private String descripcion;
    private Boolean activo;
    private List<String> permisos;

    public RolResponseDTO() {
    }

    public RolResponseDTO(Integer rolId, String nombre, String descripcion, Boolean activo, List<String> permisos) {
        this.rolId = rolId;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.activo = activo;
        this.permisos = permisos;
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

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public List<String> getPermisos() {
        return permisos;
    }

    public void setPermisos(List<String> permisos) {
        this.permisos = permisos;
    }
}
