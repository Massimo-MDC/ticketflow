package it.ticketflow.ticketflow.dto.sector;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record SectorRequestDTO(
        @NotBlank(message = "Il nome è obbligatorio")
        @Size(max = 50, message = "Il nome non può superare 50 caratteri")
        String name,

        @NotNull(message = "La capienza è obbligatoria")
        @Positive(message = "La capienza deve essere positiva")
        Integer capacity,

        @NotNull(message = "Il prezzo base è obbligatorio")
        @DecimalMin(value = "0.01", message = "Il prezzo deve essere maggiore di 0")
        BigDecimal basePrice,

        @Size(max = 255, message = "La descrizione non può superare 255 caratteri")
        String description,

        @NotNull(message = "L'ID della venue è obbligatorio")
        Long venueId
) {}