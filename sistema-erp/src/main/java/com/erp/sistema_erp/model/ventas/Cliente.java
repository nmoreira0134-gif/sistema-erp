/**
 * SISTEMA ERP - NICARAGUA
 * ARCHIVO: Cliente.java
 * PAQUETE: com.erp.sistema_erp.model.ventas
 *
 * QUÉ HACE:
 * Entidad JPA que mapea la tabla Clientes en SQL Server.
 *
 * POR QUÉ EXISTE:
 * Representa el catálogo maestro de clientes del ERP, almacenando información comercial, fiscal
 * (Cédula/RUC) y de crédito (límite y días).
 *
 * CÓMO SE CONECTA CON EL RESTO:
 * Hereda de AuditableEntity para trazabilidad y soft delete (@SQLRestriction("Activo = 1")),
 * y será referenciada por las entidades Factura y CuentaCobrar.
 */
package com.erp.sistema_erp.model.ventas;

import com.erp.sistema_erp.model.base.AuditableEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "Clientes")
@SQLRestriction("Activo = 1")
public class Cliente extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ClienteID")
    private Integer clienteId;

    @Column(name = "CodigoCliente", length = 20, nullable = false, unique = true)
    private String codigoCliente;

    @Column(name = "Nombre", length = 100, nullable = false)
    private String nombre;

    @Column(name = "Apellido", length = 100, nullable = false)
    private String apellido;

    @Column(name = "RazonSocial", length = 200)
    private String razonSocial;

    @Column(name = "TipoCliente", length = 20, nullable = false)
    private String tipoCliente;

    @Column(name = "DocumentoIdentidad", length = 30, nullable = false, unique = true)
    private String documentoIdentidad;

    @Column(name = "Email", length = 150)
    private String email;

    @Column(name = "Telefono", length = 30)
    private String telefono;

    @Column(name = "Direccion", length = 255)
    private String direccion;

    @Column(name = "Ciudad", length = 100)
    private String ciudad;

    @Column(name = "Pais", length = 100, nullable = false)
    private String pais = "Nicaragua";

    @Column(name = "LimiteCredito", precision = 12, scale = 2, nullable = false)
    private BigDecimal limiteCredito = BigDecimal.ZERO;

    @Column(name = "DiasCredito", nullable = false)
    private Integer diasCredito = 0;

    public Cliente() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cliente cliente)) return false;
        return Objects.equals(clienteId, cliente.clienteId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clienteId);
    }
}
