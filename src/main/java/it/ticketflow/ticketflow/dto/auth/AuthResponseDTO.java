package it.ticketflow.ticketflow.dto.auth;

import it.ticketflow.ticketflow.entity.enums.Role;

import java.util.UUID;

public record AuthResponseDTO(
        UUID userId,
        String email,
        String firstName,
        String lastName,
        Role role,
        String token
) {}