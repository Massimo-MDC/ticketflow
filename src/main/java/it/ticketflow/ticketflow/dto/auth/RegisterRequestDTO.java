package it.ticketflow.ticketflow.dto.auth;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record RegisterRequestDTO(
        @NotBlank(message = "Il nome è obbligatorio")
        @Size(min = 2, max = 50, message = "Il nome deve essere tra 2 e 50 caratteri")
        String firstName,

        @NotBlank(message = "Il cognome è obbligatorio")
        @Size(min = 2, max = 50, message = "Il cognome deve essere tra 2 e 50 caratteri")
        String lastName,

        @NotBlank(message = "L'email è obbligatoria")
        @Email(message = "Formato email non valido")
        String email,

        @NotBlank(message = "La password è obbligatoria")
        @Size(min = 8, message = "La password deve essere di almeno 8 caratteri")
        String password,

        @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Formato numero di telefono non valido")
        String phoneNumber,

        @Past(message = "La data di nascita deve essere nel passato")
        LocalDate dateOfBirth
) {}