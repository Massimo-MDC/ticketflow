package it.ticketflow.ticketflow.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequestDTO(
        @NotBlank(message = "Il nome è obbligatorio")
        @Size(max = 50, message = "Il nome non può superare 50 caratteri")
        String name,

        @Size(max = 255, message = "La descrizione non può superare 255 caratteri")
        String description
) {}