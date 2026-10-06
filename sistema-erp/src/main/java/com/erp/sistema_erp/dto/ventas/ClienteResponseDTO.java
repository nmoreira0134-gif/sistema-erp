/**
 * SISTEMA ERP - NICARAGUA
 * ARCHIVO: ClienteResponseDTO.java
 * PAQUETE: com.erp.sistema_erp.dto.ventas
 *
 * QUÉ HACE:
 * Objeto de transferencia que proyecta todos los datos comerciales y de auditoría de un Cliente.
 *
 * POR QUÉ EXISTE:
 * Ofrece una vista limpia y enriquecida (incluyendo campos computados como nombreCompleto y esCredito)
 * para ser consumida por la interfaz de usuario y APIs externas.
 *
 * CÓMO SE CONECTA CON EL RESTO:
 * Es devuelto en listas paginadas, búsquedas y detalles por ClienteController.
 */
package com.erp.sistema_erp.dto.ventas;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ClienteResponseDTO {

    private Integer clienteId;
    private String codigoCliente;
    private String nombre;
    private String apellido;
    private String nombreCompleto;
    private String razonSocial;
    private String tipoCliente;
    private String documentoIdentidad;
    private String email;
    private String telefono;
    private String direccion;
    private String ciudad;
    private String pais;
    private BigDecimal limiteCredito;
    private Integer diasCredito;
    private Boolean esCredito;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private String creadoPor;
    private LocalDateTime fechaModificacion;
    private String modificadoPor;

    public ClienteResponseDTO() {
    }

    public Integer getClienteId() {
        return clienteId;
    }

    public void setClienteId(Integer clienteId) {
        this.clienteId = clienteId;
    }

    public String getCodigoCliente() {
        return codigoCliente;
    }

    public void setCodigoCliente(String codigoCliente) {
        this.codigoCliente = codigoCliente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getTipoCliente() {
        return tipoCliente;
    }

    public void setTipoCliente(String tipoCliente) {
        this.tipoCliente = tipoCliente;
    }

    public String getDocumentoIdentidad() {
        return documentoIdentidad;
    }

    public void setDocumentoIdentidad(String documentoIdentidad) {
        this.documentoIdentidad = documentoIdentidad;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public BigDecimal getLimiteCredito() {
        return limiteCredito;
    }

    public void setLimiteCredito(BigDecimal limiteCredito) {
        this.limiteCredito = limiteCredito;
    }

    public Integer getDiasCredito() {
        return diasCredito;
    }

    public void setDiasCredito(Integer diasCredito) {
        this.diasCredito = diasCredito;
    }

    public Boolean getEsCredito() {
        return esCredito;
    }

    public void setEsCredito(Boolean esCredito) {
        this.esCredito = esCredito;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getCreadoPor() {
        return creadoPor;
    }

    public void setCreadoPor(String creadoPor) {
        this.creadoPor = creadoPor;
    }

    public LocalDateTime getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(LocalDateTime fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    public String getModificadoPor() {
        return modificadoPor;
    }

    public void setModificadoPor(String modificadoPor) {
        this.modificadoPor = modificadoPor;
    }
}
