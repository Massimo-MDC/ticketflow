package it.ticketflow.ticketflow.dto.event;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalTime;

public record ConcertEventRequestDTO(
        @NotBlank(message = "Il titolo è obbligatorio")
        @Size(max = 200, message = "Il titolo non può superare 200 caratteri")
        String title,

        @Size(max = 2000, message = "La descrizione non può superare 2000 caratteri")
        String description,

        @NotNull(message = "La data dell'evento è obbligatoria")
        @Future(message = "La data dell'evento deve essere nel futuro")
        LocalDate eventDate,

        @NotNull(message = "L'orario dell'evento è obbligatorio")
        LocalTime eventTime,

        @NotNull(message = "L'ID della venue è obbligatorio")
        Long venueId,

        @NotNull(message = "L'ID della categoria è obbligatorio")
        Long categoryId,

        // Campi specifici ConcertEvent
        @Size(max = 50, message = "Il genere non può superare 50 caratteri")
        String genre,

        @Size(max = 100, message = "Il nome del tour non può superare 100 caratteri")
        String tourName
) {}