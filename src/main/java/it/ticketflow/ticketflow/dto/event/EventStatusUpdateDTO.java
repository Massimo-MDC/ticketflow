package it.ticketflow.ticketflow.dto.event;

import it.ticketflow.ticketflow.entity.enums.EventStatus;
import jakarta.validation.constraints.NotNull;

public record EventStatusUpdateDTO(
        @NotNull(message = "Lo stato è obbligatorio")
        EventStatus status
) {}