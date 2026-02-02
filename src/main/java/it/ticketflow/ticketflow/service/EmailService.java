package it.ticketflow.ticketflow.service;

import it.ticketflow.ticketflow.entity.Order;
import it.ticketflow.ticketflow.entity.User;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.JsonNode;
import kong.unirest.core.Unirest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    @Value("${app.mailgun.api-key}")
    private String apiKey;

    @Value("${app.mailgun.domain}")
    private String domain;

    @Value("${app.mailgun.from}")
    private String fromEmail;

    @Async
    public void sendWelcomeEmail(User user) {
        String subject = "Benvenuto su TicketFlow!";
        String body = String.format("""
                Ciao %s,
                
                Benvenuto su TicketFlow! Siamo felici di averti con noi.
                
                Con il tuo account puoi:
                - Cercare e acquistare biglietti per eventi
                - Gestire i tuoi ordini
                - Ricevere notifiche sugli eventi
                
                Buon divertimento!
                
                Il team TicketFlow
                """, user.getFirstName());

        sendEmail(user.getEmail(), subject, body);
    }

    @Async
    public void sendOrderConfirmation(Order order) {
        User user = order.getUser();
        String subject = "Conferma Ordine #" + order.getOrderNumber();
        String body = String.format("""
                Ciao %s,
                
                Il tuo ordine è stato confermato!
                
                Numero ordine: %s
                Totale: €%.2f
                Numero biglietti: %d
                
                Puoi visualizzare i tuoi biglietti nella sezione "I miei ordini" del tuo account.
                
                Grazie per aver scelto TicketFlow!
                
                Il team TicketFlow
                """,
                user.getFirstName(),
                order.getOrderNumber(),
                order.getTotalAmount(),
                order.getTickets().size());

        sendEmail(user.getEmail(), subject, body);
    }

    private void sendEmail(String to, String subject, String body) {
        if (apiKey == null || apiKey.isBlank() || domain == null || domain.isBlank()) {
            log.warn("Mailgun non configurato. Email non inviata a: {}", to);
            return;
        }

        try {
            HttpResponse<JsonNode> response = Unirest.post("https://api.mailgun.net/v3/" + domain + "/messages")
                    .basicAuth("api", apiKey)
                    .queryString("from", fromEmail)
                    .queryString("to", to)
                    .queryString("subject", subject)
                    .queryString("text", body)
                    .asJson();

            if (response.isSuccess()) {
                log.info("Email inviata con successo a: {}", to);
            } else {
                log.error("Errore invio email a {}: {} - {}", to, response.getStatus(), response.getStatusText());
            }
        } catch (Exception e) {
            log.error("Errore durante l'invio dell'email a {}: {}", to, e.getMessage());
        }
    }
}