package it.ticketflow.ticketflow.dto.user;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UserUpdateDTO(
        @Size(min = 2, max = 50, message = "Il nome deve essere tra 2 e 50 caratteri")
        String firstName,

        @Size(min = 2, max = 50, message = "Il cognome deve essere tra 2 e 50 caratteri")
        String lastName,

        @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Formato numero di telefono non valido")
        String phoneNumber,

        @Past(message = "La data di nascita deve essere nel passato")
        LocalDate dateOfBirth
) {}