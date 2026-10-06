/**
 * SISTEMA ERP - NICARAGUA
 * ARCHIVO: MatrizPermisoDTO.java
 * PAQUETE: com.erp.sistema_erp.dto.seguridad
 *
 * QUÉ HACE:
 * Modela un renglón de la matriz de permisos para una pantalla concreta indicando qué acciones tiene habilitadas un rol.
 *
 * POR QUÉ EXISTE:
 * Facilita la vista tabular en el frontend donde se visualiza qué pantallas y qué operaciones (crear, editar, anular, etc.) puede realizar un rol.
 *
 * CÓMO SE CONECTA CON EL RESTO:
 * Es producido por RolService y consumido en el módulo roles.js.
 */
package com.erp.sistema_erp.dto.seguridad;

import java.util.List;

public class MatrizPermisoDTO {

    private Integer pantallaId;
    private String codigoPantalla;
    private String nombrePantalla;
    private String modulo;
    private List<String> permisosAsignados;

    public MatrizPermisoDTO() {
    }

    public MatrizPermisoDTO(Integer pantallaId, String codigoPantalla, String nombrePantalla, String modulo, List<String> permisosAsignados) {
        this.pantallaId = pantallaId;
        this.codigoPantalla = codigoPantalla;
        this.nombrePantalla = nombrePantalla;
        this.modulo = modulo;
        this.permisosAsignados = permisosAsignados;
    }

    public Integer getPantallaId() {
        return pantallaId;
    }

    public void setPantallaId(Integer pantallaId) {
        this.pantallaId = pantallaId;
    }

    public String getCodigoPantalla() {
        return codigoPantalla;
    }

    public void setCodigoPantalla(String codigoPantalla) {
        this.codigoPantalla = codigoPantalla;
    }

    public String getNombrePantalla() {
        return nombrePantalla;
    }

    public void setNombrePantalla(String nombrePantalla) {
        this.nombrePantalla = nombrePantalla;
    }

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }

    public List<String> getPermisosAsignados() {
        return permisosAsignados;
    }

    public void setPermisosAsignados(List<String> permisosAsignados) {
        this.permisosAsignados = permisosAsignados;
    }
}
