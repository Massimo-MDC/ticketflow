package it.ticketflow.ticketflow.dto.error;

import java.time.LocalDateTime;

public record ErrorResponseDTO(
        String code,
        String message,
        LocalDateTime timestamp
) {}