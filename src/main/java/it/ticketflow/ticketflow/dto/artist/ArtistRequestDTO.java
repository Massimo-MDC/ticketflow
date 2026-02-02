package it.ticketflow.ticketflow.dto.artist;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ArtistRequestDTO(
        @NotBlank(message = "Il nome d'arte è obbligatorio")
        @Size(max = 100, message = "Il nome non può superare 100 caratteri")
        String stageName,

        @Size(max = 1000, message = "La bio non può superare 1000 caratteri")
        String bio,

        @Size(max = 50, message = "Il genere non può superare 50 caratteri")
        String genre
) {}