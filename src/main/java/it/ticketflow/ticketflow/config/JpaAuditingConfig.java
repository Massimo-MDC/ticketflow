package it.ticketflow.ticketflow.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
    // Abilita @CreatedDate e @LastModifiedDate nelle entità
}