/**
 * SISTEMA ERP - NICARAGUA
 * ARCHIVO: Usuario.java
 * PAQUETE: com.erp.sistema_erp.model.seguridad
 *
 * QUÉ HACE:
 * Representa la entidad JPA para la tabla Usuarios del sistema ERP.
 *
 * POR QUÉ EXISTE:
 * Contiene las credenciales cifradas (BCrypt), datos de contacto y rol asignado a cada usuario
 * para autenticación y auditoría en cada módulo.
 *
 * CÓMO SE CONECTA CON EL RESTO:
 * Pertenece a un Rol (@ManyToOne), es cargado por CustomUserDetailsService en la validación JWT
 * y su 'username' se registra en el campo CreadoPor / ModificadoPor de todas las transacciones.
 */
package com.erp.sistema_erp.model.seguridad;

import com.erp.sistema_erp.model.base.AuditableEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.SQLRestriction;

import java.util.Objects;

@Entity
@Table(name = "Usuarios")
@SQLRestriction("Activo = 1")
public class Usuario extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UsuarioID")
    private Integer usuarioId;

    @Column(name = "Username", length = 50, nullable = false, unique = true)
    private String username;

    @Column(name = "PasswordHash", length = 255, nullable = false)
    private String passwordHash;

    @Column(name = "NombreCompleto", length = 150, nullable = false)
    private String nombreCompleto;

    @Column(name = "Email", length = 100, nullable = false, unique = true)
    private String email;

    @Column(name = "Telefono", length = 30)
    private String telefono;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "RolID", nullable = false)
    private Rol rol;

    public Usuario() {
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
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

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Usuario usuario)) return false;
        return Objects.equals(usuarioId, usuario.usuarioId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(usuarioId);
    }
}
