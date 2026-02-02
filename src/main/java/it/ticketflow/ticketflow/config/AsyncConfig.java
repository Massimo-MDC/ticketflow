package it.ticketflow.ticketflow.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class AsyncConfig {
    // Abilita l'esecuzione asincrona per l'invio email
}
