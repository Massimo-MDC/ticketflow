package it.ticketflow.ticketflow.dto.event;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalTime;

public record TheaterEventRequestDTO(
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

        // Campi specifici TheaterEvent
        @Size(max = 100, message = "Il nome del regista non può superare 100 caratteri")
        String director,

        @Positive(message = "La durata deve essere positiva")
        Integer durationMinutes,

        @Size(max = 20, message = "La restrizione d'età non può superare 20 caratteri")
        String ageRestriction
) {}