/**
 * SISTEMA ERP - NICARAGUA
 * ARCHIVO: PantallaMenuDTO.java
 * PAQUETE: com.erp.sistema_erp.dto.seguridad
 *
 * QUÉ HACE:
 * DTO simplificado que transporta la metadata de una pantalla para renderizar enlaces en el sidebar.
 *
 * POR QUÉ EXISTE:
 * Evita exponer campos de infraestructura o auditoría al construir el árbol de navegación web.
 *
 * CÓMO SE CONECTA CON EL RESTO:
 * Es utilizado por PantallaService y enviado dentro de LoginResponseDTO y /api/pantallas/menu.
 */
package com.erp.sistema_erp.dto.seguridad;

public class PantallaMenuDTO {

    private Integer pantallaId;
    private String codigo;
    private String nombre;
    private String ruta;
    private String icono;
    private String modulo;
    private Integer orden;

    public PantallaMenuDTO() {
    }

    public PantallaMenuDTO(Integer pantallaId, String codigo, String nombre, String ruta, String icono, String modulo, Integer orden) {
        this.pantallaId = pantallaId;
        this.codigo = codigo;
        this.nombre = nombre;
        this.ruta = ruta;
        this.icono = icono;
        this.modulo = modulo;
        this.orden = orden;
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
}
