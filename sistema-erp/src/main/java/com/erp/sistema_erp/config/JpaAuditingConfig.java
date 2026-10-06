/**
 * SISTEMA ERP - NICARAGUA
 * ARCHIVO: JpaAuditingConfig.java
 * PAQUETE: com.erp.sistema_erp.config
 *
 * QUÉ HACE:
 * Habilita la auditoría automática de Spring Data JPA con la anotación @EnableJpaAuditing.
 *
 * POR QUÉ EXISTE:
 * Activa los listeners que escuchan eventos de creación y actualización para AuditableEntity.
 *
 * CÓMO SE CONECTA CON EL RESTO:
 * Registra el Bean auditorProvider que suministra la instancia de AuditorAwareImpl.
 */
package com.erp.sistema_erp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaAuditingConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return new AuditorAwareImpl();
    }
}
