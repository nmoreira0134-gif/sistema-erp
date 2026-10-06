/**
 * SISTEMA ERP - NICARAGUA
 * ARCHIVO: AuditorAwareImpl.java
 * PAQUETE: com.erp.sistema_erp.config
 *
 * QUÉ HACE:
 * Implementación de AuditorAware para obtener dinámicamente el nombre de usuario
 * autenticado en el contexto de seguridad actual.
 *
 * POR QUÉ EXISTE:
 * Asigna automáticamente los campos CreadoPor y ModificadoPor en AuditableEntity,
 * garantizando trazabilidad y auditoría sin intervención manual.
 *
 * CÓMO SE CONECTA CON EL RESTO:
 * Es registrada como Bean en JpaAuditingConfig y consultada por JPA en cada persist/update.
 */
package com.erp.sistema_erp.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return Optional.of("SYSTEM");
        }

        return Optional.ofNullable(authentication.getName());
    }
}
