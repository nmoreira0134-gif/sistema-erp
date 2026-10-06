/**
 * SISTEMA ERP - NICARAGUA
 * ARCHIVO: ClienteCreateDTO.java
 * PAQUETE: com.erp.sistema_erp.dto.ventas
 *
 * QUÉ HACE:
 * Encapsula y valida los datos para dar de alta un nuevo cliente en el sistema ERP.
 *
 * POR QUÉ EXISTE:
 * Asegura la integridad de los datos de entrada según las normas mercantiles y fiscales nicaragüenses,
 * validando que los nombres, tipo de cliente, documento y condiciones de crédito sean válidos.
 *
 * CÓMO SE CONECTA CON EL RESTO:
 * Es recibido como cuerpo de la petición en POST /api/clientes y procesado por ClienteService.
 */
package com.erp.sistema_erp.dto.ventas;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class ClienteCreateDTO {

    @Size(max = 20, message = "El código de cliente no puede exceder 20 caracteres")
    private String codigoCliente;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100, message = "El apellido no puede exceder 100 caracteres")
    private String apellido;

    @Size(max = 200, message = "La razón social no puede exceder 200 caracteres")
    private String razonSocial;

    @NotBlank(message = "El tipo de cliente es obligatorio")
    @Pattern(regexp = "^(Natural|Juridico)$", message = "El tipo de cliente debe ser 'Natural' o 'Juridico'")
    private String tipoCliente;

    @NotBlank(message = "El documento de identidad es obligatorio")
    @Size(max = 30, message = "El documento de identidad no puede exceder 30 caracteres")
    private String documentoIdentidad;

    @Email(message = "El formato de correo electrónico no es válido")
    @Size(max = 150, message = "El correo no puede exceder 150 caracteres")
    private String email;

    @Size(max = 30, message = "El teléfono no puede exceder 30 caracteres")
    private String telefono;

    @Size(max = 255, message = "La dirección no puede exceder 255 caracteres")
    private String direccion;

    @Size(max = 100, message = "La ciudad no puede exceder 100 caracteres")
    private String ciudad;

    @Size(max = 100, message = "El país no puede exceder 100 caracteres")
    private String pais;

    @DecimalMin(value = "0.0", message = "El límite de crédito no puede ser negativo")
    private BigDecimal limiteCredito;

    @Min(value = 0, message = "Los días de crédito no pueden ser negativos")
    private Integer diasCredito;

    public ClienteCreateDTO() {
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
}
