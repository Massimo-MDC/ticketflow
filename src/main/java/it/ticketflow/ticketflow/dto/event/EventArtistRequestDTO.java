package it.ticketflow.ticketflow.dto.event;

import jakarta.validation.constraints.NotNull;

public record EventArtistRequestDTO(
        @NotNull(message = "L'ID dell'artista è obbligatorio")
        Long artistId,

        Boolean isHeadliner,

        String performanceOrder
) {}