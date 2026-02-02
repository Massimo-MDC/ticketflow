package it.ticketflow.ticketflow.dto.event;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record EventSectorRequestDTO(
        @NotNull(message = "L'ID del settore è obbligatorio")
        Long sectorId,

        @NotNull(message = "Il numero di biglietti disponibili è obbligatorio")
        @PositiveOrZero(message = "Il numero di biglietti deve essere zero o positivo")
        Integer availableTickets,

        @NotNull(message = "Il prezzo corrente è obbligatorio")
        @DecimalMin(value = "0.01", message = "Il prezzo deve essere maggiore di 0")
        BigDecimal currentPrice
) {}