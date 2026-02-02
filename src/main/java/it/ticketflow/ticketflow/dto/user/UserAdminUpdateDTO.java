package it.ticketflow.ticketflow.dto.user;

import it.ticketflow.ticketflow.entity.enums.Role;
import jakarta.validation.constraints.NotNull;

public record UserAdminUpdateDTO(
        @NotNull(message = "Il ruolo è obbligatorio")
        Role role,

        Boolean enabled
) {}