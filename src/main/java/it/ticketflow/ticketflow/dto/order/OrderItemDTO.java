package it.ticketflow.ticketflow.dto.order;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderItemDTO(
        @NotNull(message = "L'ID dell'EventSector è obbligatorio")
        Long eventSectorId,

        @NotNull(message = "La quantità è obbligatoria")
        @Positive(message = "La quantità deve essere positiva")
        Integer quantity
) {}