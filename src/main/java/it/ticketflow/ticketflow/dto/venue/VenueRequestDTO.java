package it.ticketflow.ticketflow.dto.venue;

import jakarta.validation.constraints.*;

public record VenueRequestDTO(
        @NotBlank(message = "Il nome è obbligatorio")
        @Size(max = 100, message = "Il nome non può superare 100 caratteri")
        String name,

        @NotBlank(message = "L'indirizzo è obbligatorio")
        @Size(max = 200, message = "L'indirizzo non può superare 200 caratteri")
        String address,

        @NotBlank(message = "La città è obbligatoria")
        @Size(max = 100, message = "La città non può superare 100 caratteri")
        String city,

        @Size(max = 100, message = "La regione non può superare 100 caratteri")
        String region,

        @NotBlank(message = "Il paese è obbligatorio")
        @Size(max = 100, message = "Il paese non può superare 100 caratteri")
        String country,

        @Size(max = 10, message = "Il CAP non può superare 10 caratteri")
        String zipCode,

        Double latitude,

        Double longitude,

        @NotNull(message = "La capienza totale è obbligatoria")
        @Positive(message = "La capienza deve essere positiva")
        Integer totalCapacity,

        @Size(max = 1000, message = "La descrizione non può superare 1000 caratteri")
        String description
) {}