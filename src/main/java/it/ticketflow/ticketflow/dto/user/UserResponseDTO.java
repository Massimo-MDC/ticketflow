package it.ticketflow.ticketflow.dto.user;

import it.ticketflow.ticketflow.entity.User;
import it.ticketflow.ticketflow.entity.enums.Role;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponseDTO(
        UUID id,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        LocalDate dateOfBirth,
        String profileImageUrl,
        LocalDateTime registrationDate,
        Role role,
        boolean enabled
) {
    public static UserResponseDTO fromEntity(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getDateOfBirth(),
                user.getProfileImageUrl(),
                user.getRegistrationDate(),
                user.getRole(),
                user.isEnabled()
        );
    }
}