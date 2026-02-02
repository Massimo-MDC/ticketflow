package it.ticketflow.ticketflow.service;

import it.ticketflow.ticketflow.dto.auth.AuthResponseDTO;
import it.ticketflow.ticketflow.dto.auth.LoginRequestDTO;
import it.ticketflow.ticketflow.dto.auth.RegisterRequestDTO;
import it.ticketflow.ticketflow.entity.User;
import it.ticketflow.ticketflow.exception.BadRequestException;
import it.ticketflow.ticketflow.exception.UnauthorizedException;
import it.ticketflow.ticketflow.repository.UserRepository;
import it.ticketflow.ticketflow.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO request) {
        // Verifica che l'email non sia già registrata
        if (userRepository.existsByEmail(request.email())) {
            throw new BadRequestException("Email già registrata");
        }

        // Crea il nuovo utente
        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .phoneNumber(request.phoneNumber())
                .dateOfBirth(request.dateOfBirth())
                .build();

        // Salva l'utente
        User savedUser = userRepository.save(user);

        // Genera il token JWT
        String token = jwtService.generateToken(savedUser);

        // Restituisci la risposta
        return new AuthResponseDTO(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getRole(),
                token
        );
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        // Trova l'utente per email
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UnauthorizedException("Credenziali non valide"));

        // Verifica la password
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new UnauthorizedException("Credenziali non valide");
        }

        // Verifica che l'utente sia abilitato
        if (!user.isEnabled()) {
            throw new UnauthorizedException("Account disabilitato");
        }

        // Genera il token JWT
        String token = jwtService.generateToken(user);

        // Restituisci la risposta
        return new AuthResponseDTO(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole(),
                token
        );
    }
}